package com.ethan.subject.domain.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.ethan.subject.common.entity.PageResult;
import com.ethan.subject.common.entity.PageResult;
import com.ethan.subject.common.enums.IsDeletedFlagEnum;
//import com.ethan.subject.common.util.IdWorkerUtil;
//import com.ethan.subject.common.util.LoginUtil;
import com.ethan.subject.domain.convert.SubjectCategoryConverter;
import com.ethan.subject.domain.convert.SubjectInfoConverter;
import com.ethan.subject.domain.entity.SubjectCategoryBO;
import com.ethan.subject.domain.entity.SubjectInfoBO;
import com.ethan.subject.domain.entity.SubjectOptionBO;
import com.ethan.subject.domain.handler.subject.SubjectTypeHandler;
import com.ethan.subject.domain.handler.subject.SubjectTypeHandlerFactory;
//import com.ethan.subject.domain.redis.RedisUtil;
import com.ethan.subject.domain.service.SubjectCategoryDomainService;
import com.ethan.subject.domain.service.SubjectInfoDomainService;
//import com.ethan.subject.domain.service.SubjectLikedDomainService;
import com.ethan.subject.infra.basic.entity.*;
import com.ethan.subject.infra.basic.service.*;
//import com.ethan.subject.infra.entity.UserInfo;
//import com.ethan.subject.infra.rpc.UserRpc;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class SubjectInfoDomainServiceImpl implements SubjectInfoDomainService {

    @Resource
    private SubjectInfoService subjectInfoService;

    @Resource
    private SubjectMappingService subjectMappingService;

    @Resource
    private SubjectLabelService subjectLabelService;

    @Resource
    private SubjectTypeHandlerFactory subjectTypeHandlerFactory;

//    @Resource
//    private SubjectEsService subjectEsService;
//
//    @Resource
//    private SubjectLikedDomainService subjectLikedDomainService;
//
//    @Resource
//    private UserRpc userRpc;
//
//    @Resource
//    private RedisUtil redisUtil;

    private static final String RANK_KEY = "subject_rank";


    @Override
    @Transactional(rollbackFor = Exception.class) // 事务注解，用于事务控制
    public void add(SubjectInfoBO subjectInfoBO) {
        if (log.isInfoEnabled()) {
            log.info("SubjectInfoDomainServiceImpl.add.bo:{}", JSON.toJSONString(subjectInfoBO));
        }
        SubjectInfo subjectInfo = SubjectInfoConverter.INSTANCE.convertBoToInfo(subjectInfoBO); // 转换为实体类
        subjectInfo.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode()); // 默认未删除
        subjectInfoService.insert(subjectInfo); // 插入数据库
        SubjectTypeHandler handler = subjectTypeHandlerFactory.getHandler(subjectInfo.getSubjectType()); // 获取处理器
        subjectInfoBO.setId(subjectInfo.getId()); // 设置id
        handler.add(subjectInfoBO); // 调用处理器添加
        List<Integer> categoryIds = subjectInfoBO.getCategoryIds(); // 获取分类id
        List<Integer> labelIds = subjectInfoBO.getLabelIds(); // 获取标签id
        List<SubjectMapping> mappingList = new LinkedList<>();
        // 转换为映射实体类
        categoryIds.forEach(categoryId -> {
            labelIds.forEach(labelId -> {
                SubjectMapping subjectMapping = new SubjectMapping();
                subjectMapping.setSubjectId(subjectInfo.getId());
                subjectMapping.setCategoryId(Long.valueOf(categoryId));
                subjectMapping.setLabelId(Long.valueOf(labelId));
                subjectMapping.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
                mappingList.add(subjectMapping);
            });
        });
        subjectMappingService.batchInsert(mappingList); // 插入映射关系
//        //同步到es
//        SubjectInfoEs subjectInfoEs = new SubjectInfoEs();
//        subjectInfoEs.setDocId(new IdWorkerUtil(1, 1, 1).nextId());
//        subjectInfoEs.setSubjectId(subjectInfo.getId());
//        subjectInfoEs.setSubjectAnswer(subjectInfoBO.getSubjectAnswer());
//        subjectInfoEs.setCreateTime(new Date().getTime());
//        subjectInfoEs.setCreateUser("鸡翅");
//        subjectInfoEs.setSubjectName(subjectInfo.getSubjectName());
//        subjectInfoEs.setSubjectType(subjectInfo.getSubjectType());
//        subjectEsService.insert(subjectInfoEs);
//        //redis放入zadd计入排行榜
//        redisUtil.addScore(RANK_KEY, LoginUtil.getLoginId(), 1);
    }

    @Override
    public PageResult<SubjectInfoBO> getSubjectPage(SubjectInfoBO subjectInfoBO) {
        PageResult<SubjectInfoBO> pageResult = new PageResult<>();
        pageResult.setPageNo(subjectInfoBO.getPageNo());
        pageResult.setPageSize(subjectInfoBO.getPageSize());
        int start = (subjectInfoBO.getPageNo() - 1) * subjectInfoBO.getPageSize();
        SubjectInfo subjectInfo = SubjectInfoConverter.INSTANCE.convertBoToInfo(subjectInfoBO);
        int count = subjectInfoService.countByCondition(subjectInfo, subjectInfoBO.getCategoryId()
                , subjectInfoBO.getLabelId());
        if (count == 0) {
            return pageResult;
        }
        List<SubjectInfo> subjectInfoList = subjectInfoService.queryPage(subjectInfo, subjectInfoBO.getCategoryId()
                , subjectInfoBO.getLabelId(), start, subjectInfoBO.getPageSize());
        List<SubjectInfoBO> subjectInfoBOS = SubjectInfoConverter.INSTANCE.convertListInfoToBO(subjectInfoList);
        subjectInfoBOS.forEach(info -> {
            SubjectMapping subjectMapping = new SubjectMapping();
            subjectMapping.setSubjectId(info.getId());
            List<SubjectMapping> mappingList = subjectMappingService.queryLabelId(subjectMapping);
            List<Long> labelIds = mappingList.stream().map(SubjectMapping::getLabelId).collect(Collectors.toList());
            List<SubjectLabel> labelList = subjectLabelService.batchQueryById(labelIds);
            List<String> labelNames = labelList.stream().map(SubjectLabel::getLabelName).collect(Collectors.toList());
            info.setLabelName(labelNames);
        });
        pageResult.setRecords(subjectInfoBOS);
        pageResult.setTotal(count);
        return pageResult;
    }

    @Override
    public SubjectInfoBO querySubjectInfo(SubjectInfoBO subjectInfoBO) {
        SubjectInfo subjectInfo = subjectInfoService.queryById(subjectInfoBO.getId());
        SubjectTypeHandler handler = subjectTypeHandlerFactory.getHandler(subjectInfo.getSubjectType());
        SubjectOptionBO optionBO = handler.query(subjectInfo.getId().intValue());
        SubjectInfoBO bo = SubjectInfoConverter.INSTANCE.convertOptionAndInfoToBo(optionBO, subjectInfo);
        SubjectMapping subjectMapping = new SubjectMapping();
        subjectMapping.setSubjectId(subjectInfo.getId());
        subjectMapping.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
        List<SubjectMapping> mappingList = subjectMappingService.queryLabelId(subjectMapping);
        List<Long> labelIdList = mappingList.stream().map(SubjectMapping::getLabelId).collect(Collectors.toList());
        List<SubjectLabel> labelList = subjectLabelService.batchQueryById(labelIdList);
        List<String> labelNameList = labelList.stream().map(SubjectLabel::getLabelName).collect(Collectors.toList());
        bo.setLabelName(labelNameList);
//        bo.setLiked(subjectLikedDomainService.isLiked(subjectInfoBO.getId().toString(), LoginUtil.getLoginId()));
//        bo.setLikedCount(subjectLikedDomainService.getLikedCount(subjectInfoBO.getId().toString()));
//        assembleSubjectCursor(subjectInfoBO, bo);
        return bo;
    }

//    private void assembleSubjectCursor(SubjectInfoBO subjectInfoBO, SubjectInfoBO bo) {
//        Long categoryId = subjectInfoBO.getCategoryId();
//        Long labelId = subjectInfoBO.getLabelId();
//        Long subjectId = subjectInfoBO.getId();
//        if (Objects.isNull(categoryId) || Objects.isNull(labelId)) {
//            return;
//        }
//        Long nextSubjectId = subjectInfoService.querySubjectIdCursor(subjectId, categoryId, labelId, 1);
//        bo.setNextSubjectId(nextSubjectId);
//        Long lastSubjectId = subjectInfoService.querySubjectIdCursor(subjectId, categoryId, labelId, 0);
//        bo.setLastSubjectId(lastSubjectId);
//    }
//
//    @Override
//    public PageResult<SubjectInfoEs> getSubjectPageBySearch(SubjectInfoBO subjectInfoBO) {
//        SubjectInfoEs subjectInfoEs = new SubjectInfoEs();
//        subjectInfoEs.setPageNo(subjectInfoBO.getPageNo());
//        subjectInfoEs.setPageSize(subjectInfoBO.getPageSize());
//        subjectInfoEs.setKeyWord(subjectInfoBO.getKeyWord());
//        return subjectEsService.querySubjectList(subjectInfoEs);
//    }
//
//    @Override
//    public List<SubjectInfoBO> getContributeList() {
//        Set<ZSetOperations.TypedTuple<String>> typedTuples = redisUtil.rankWithScore(RANK_KEY, 0, 5);
//        if (log.isInfoEnabled()) {
//            log.info("getContributeList.typedTuples:{}", JSON.toJSONString(typedTuples));
//        }
//        if (CollectionUtils.isEmpty(typedTuples)) {
//            return Collections.emptyList();
//        }
//        List<SubjectInfoBO> boList = new LinkedList<>();
//        typedTuples.forEach((rank -> {
//            SubjectInfoBO subjectInfoBO = new SubjectInfoBO();
//            subjectInfoBO.setSubjectCount(rank.getScore().intValue());
//            UserInfo userInfo = userRpc.getUserInfo(rank.getValue());
//            subjectInfoBO.setCreateUser(userInfo.getNickName());
//            subjectInfoBO.setCreateUserAvatar(userInfo.getAvatar());
//            boList.add(subjectInfoBO);
//        }));
//        return boList;
//    }

}
