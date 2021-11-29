package com.example.springsecurityregistration.service;

import com.example.springsecurityregistration.persistence.dao.RoleDao;
import com.example.springsecurityregistration.persistence.model.Role;
import com.example.springsecurityregistration.persistence.model.RoleName;
import com.example.springsecurityregistration.web.error.UserHasNotRoleException;
import com.example.springsecurityregistration.web.error.UserHasRoleException;
import com.example.springsecurityregistration.web.error.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleDao roleDao;

    @Autowired
    public RoleServiceImpl(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    public void addRoleByUserId(long userId, RoleName roleName) {
        Set<Role> roles = roleDao.getRolesByUserId(userId);

        if (roles.isEmpty()) {
            throw new UserNotFoundException("No user with id: " + userId);
        } else if (roles.contains(new Role(roleName))) {
            throw new UserHasRoleException("User already has this role");
        }
        roleDao.addRoleById(userId, roleName.name());
    }

    @Override
    public void removeRoleByUserId(long userId, RoleName roleName) {
        Set<Role> roles = roleDao.getRolesByUserId(userId);

        if (roles.isEmpty()) {
            throw new UserNotFoundException("No user with id: " + userId);
        } else if (!roles.contains(new Role(roleName))) {
            throw new UserHasNotRoleException("User has not this role");
        }
        roleDao.removeRoleById(userId, roleName.name());
    }
}
