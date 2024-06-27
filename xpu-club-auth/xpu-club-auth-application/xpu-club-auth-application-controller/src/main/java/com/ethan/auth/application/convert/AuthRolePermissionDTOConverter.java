package com.ethan.auth.application.convert;

import com.ethan.auth.application.dto.AuthPermissionDTO;
import com.ethan.auth.application.dto.AuthRolePermissionDTO;
import com.ethan.auth.domain.entity.AuthPermissionBO;
import com.ethan.auth.domain.entity.AuthRolePermissionBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 权限dto转换器
 * 
 * @author: ChickenWing
 * @date: 2023/10/8
 */
@Mapper
public interface AuthRolePermissionDTOConverter {

    AuthRolePermissionDTOConverter INSTANCE = Mappers.getMapper(AuthRolePermissionDTOConverter.class);

    AuthRolePermissionBO convertDTOToBO(AuthRolePermissionDTO authRolePermissionDTO);

}
