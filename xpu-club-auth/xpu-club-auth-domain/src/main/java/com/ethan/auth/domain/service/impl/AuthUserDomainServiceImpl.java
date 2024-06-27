package com.ethan.auth.domain.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.google.gson.Gson;
import com.ethan.auth.common.enums.AuthUserStatusEnum;
import com.ethan.auth.common.enums.IsDeletedFlagEnum;
import com.ethan.auth.domain.constants.AuthConstant;
import com.ethan.auth.domain.convert.AuthUserBOConverter;
import com.ethan.auth.domain.entity.AuthUserBO;
import com.ethan.auth.domain.redis.RedisUtil;
import com.ethan.auth.domain.service.AuthUserDomainService;
import com.ethan.auth.infra.basic.entity.*;
import com.ethan.auth.infra.basic.service.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class AuthUserDomainServiceImpl implements AuthUserDomainService {

    @Resource
    private AuthUserService authUserService;

    @Resource
    private AuthUserRoleService authUserRoleService;

    @Resource
    private AuthPermissionService authPermissionService;

    @Resource
    private AuthRolePermissionService authRolePermissionService;

    @Resource
    private AuthRoleService authRoleService;

    private String salt = "chicken";

    @Resource
    private RedisUtil redisUtil;

    private String authPermissionPrefix = "auth.permission";

    private String authRolePrefix = "auth.role";

    private static final String LOGIN_PREFIX = "loginCode";

    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public Boolean register(AuthUserBO authUserBO) {
        //校验用户是否存在
        AuthUser existAuthUser = new AuthUser();
        existAuthUser.setUserName(authUserBO.getUserName());
        List<AuthUser> existUser = authUserService.queryByCondition(existAuthUser);
        if (existUser.size() > 0) {
            return true;
        }
        AuthUser authUser = AuthUserBOConverter.INSTANCE.convertBOToEntity(authUserBO);
        if (StringUtils.isNotBlank(authUser.getPassword())) {
            authUser.setPassword(SaSecureUtil.md5BySalt(authUser.getPassword(), salt));
        }
        if (StringUtils.isBlank(authUser.getAvatar())) {
            authUser.setAvatar("http://117.72.10.84:9000/user/icon/微信图片_20231203153718(1).png");
        }
        authUser.setStatus(AuthUserStatusEnum.OPEN.getCode());
        authUser.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
        Integer count = authUserService.insert(authUser);

        //建立一个初步的角色的关联
        AuthRole authRole = new AuthRole();
        authRole.setRoleKey(AuthConstant.NORMAL_USER);
        AuthRole roleResult = authRoleService.queryByCondition(authRole);
        Long roleId = roleResult.getId();
        Long userId = authUser.getId();
        AuthUserRole authUserRole = new AuthUserRole();
        authUserRole.setUserId(userId);
        authUserRole.setRoleId(roleId);
        authUserRole.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
        authUserRoleService.insert(authUserRole);

        //建立缓存
        String roleKey = redisUtil.buildKey(authRolePrefix, authUser.getUserName()); //缓存角色列表的key
        List<AuthRole> roleList = new LinkedList<>(); // 角色列表
        roleList.add(authRole); // 加入普通用户角色
        redisUtil.set(roleKey, new Gson().toJson(roleList)); // 缓存角色列表
        //根据roleId查权限
        AuthRolePermission authRolePermission = new AuthRolePermission(); // 角色权限关联
        authRolePermission.setRoleId(roleId); // 角色id
        List<AuthRolePermission> rolePermissionList = authRolePermissionService.
                queryByCondition(authRolePermission); // 根据角色id查询权限

        List<Long> permissionIdList = rolePermissionList.stream()
                .map(AuthRolePermission::getPermissionId).collect(Collectors.toList()); // 权限id列表
        List<AuthPermission> permissionList = authPermissionService.queryByRoleList(permissionIdList); // 根据权限id列表查询权限列表
        String permissionKey = redisUtil.buildKey(authPermissionPrefix, authUser.getUserName()); // 缓存权限列表的key
        redisUtil.set(permissionKey, new Gson().toJson(permissionList)); // 缓存权限列表

        return count > 0; // 返回是否注册成功
    }

    @Override
    public Boolean update(AuthUserBO authUserBO) {
        AuthUser authUser = AuthUserBOConverter.INSTANCE.convertBOToEntity(authUserBO);
        Integer count = authUserService.updateByUserName(authUser);
        return count > 0;
    }

    @Override
    public Boolean deleteById(AuthUserBO authUserBO) {
        AuthUser authUser = new AuthUser();
        authUser.setId(authUserBO.getId());
        authUser.setIsDeleted(IsDeletedFlagEnum.DELETED.getCode());
        Integer count = authUserService.update(authUser);
        //有任何的更新，都要与缓存进行同步的修改
        return count > 0;
    }

    // 登录
    @Override
    public SaTokenInfo doLogin(String validCode) {
        // 构建登录验证码在 Redis 中的键
        String loginKey = redisUtil.buildKey(LOGIN_PREFIX, validCode);
        // 从 Redis 中根据键获取 openId
        String openId = redisUtil.get(loginKey);
        // 如果获取到的 openId 为空，说明验证码已过期
        if (StringUtils.isBlank(openId)) {
            // 登录失败，返回 null
            return null;
        }
        // 创建一个 AuthUserBO 对象
        AuthUserBO authUserBO = new AuthUserBO();
        // 设置用户名
        authUserBO.setUserName(openId);
        // 调用注册用户的方法
        this.register(authUserBO);
        // 执行登录操作
        StpUtil.login(openId);
        // 获取 token 信息
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        // 返回 token 信息
        return tokenInfo;
    }

    @Override
    public AuthUserBO getUserInfo(AuthUserBO authUserBO) {
        AuthUser authUser = new AuthUser();
        authUser.setUserName(authUserBO.getUserName());
        List<AuthUser> userList = authUserService.queryByCondition(authUser);
        if(CollectionUtils.isEmpty(userList)){
            return new AuthUserBO();
        }
        AuthUser user = userList.get(0);
        return AuthUserBOConverter.INSTANCE.convertEntityToBO(user);
    }
}
