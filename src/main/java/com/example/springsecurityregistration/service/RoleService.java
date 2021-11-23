package com.example.springsecurityregistration.service;

import com.example.springsecurityregistration.persistence.model.RoleName;

public interface RoleService {

    void addRoleByUserId(long userId, RoleName roleName);

    void removeRoleByUserId(long userId, RoleName roleName);
}
