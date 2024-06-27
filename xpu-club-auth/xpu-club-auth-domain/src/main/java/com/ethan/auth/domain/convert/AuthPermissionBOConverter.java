package com.ethan.auth.domain.convert;

import com.ethan.auth.domain.entity.AuthPermissionBO;
import com.ethan.auth.domain.entity.AuthRoleBO;
import com.ethan.auth.infra.basic.entity.AuthPermission;
import com.ethan.auth.infra.basic.entity.AuthRole;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 权限bo转换器
 * 
 * @author: ChickenWing
 * @date: 2023/10/8
 */
@Mapper
public interface AuthPermissionBOConverter {

    AuthPermissionBOConverter INSTANCE = Mappers.getMapper(AuthPermissionBOConverter.class);

    AuthPermission convertBOToEntity(AuthPermissionBO authPermissionBO);

}
