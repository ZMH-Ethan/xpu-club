package com.ethan.auth.application.convert;

import com.ethan.auth.application.dto.AuthPermissionDTO;
import com.ethan.auth.application.dto.AuthRoleDTO;
import com.ethan.auth.domain.entity.AuthPermissionBO;
import com.ethan.auth.domain.entity.AuthRoleBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 权限dto转换器
 * 
 * @author: ChickenWing
 * @date: 2023/10/8
 */
@Mapper
public interface AuthPermissionDTOConverter {

    AuthPermissionDTOConverter INSTANCE = Mappers.getMapper(AuthPermissionDTOConverter.class);

    AuthPermissionBO convertDTOToBO(AuthPermissionDTO authPermissionDTO);

}
