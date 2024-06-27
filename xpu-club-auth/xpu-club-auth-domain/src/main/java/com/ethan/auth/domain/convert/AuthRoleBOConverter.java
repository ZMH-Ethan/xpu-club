package com.ethan.auth.domain.convert;

import com.ethan.auth.domain.entity.AuthRoleBO;
import com.ethan.auth.domain.entity.AuthUserBO;
import com.ethan.auth.infra.basic.entity.AuthRole;
import com.ethan.auth.infra.basic.entity.AuthUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 角色bo转换器
 * 
 * @author: ChickenWing
 * @date: 2023/10/8
 */
@Mapper
public interface AuthRoleBOConverter {

    AuthRoleBOConverter INSTANCE = Mappers.getMapper(AuthRoleBOConverter.class);

    AuthRole convertBOToEntity(AuthRoleBO authRoleBO);

}
