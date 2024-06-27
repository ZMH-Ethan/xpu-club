package com.ethan.auth.domain.service;

import com.ethan.auth.domain.entity.AuthPermissionBO;
import com.ethan.auth.domain.entity.AuthRolePermissionBO;

/**
 * 角色领域service
 * 
 * @author: ChickenWing
 * @date: 2023/11/1
 */
public interface AuthRolePermissionDomainService {

    Boolean add(AuthRolePermissionBO authRolePermissionBO);

}
