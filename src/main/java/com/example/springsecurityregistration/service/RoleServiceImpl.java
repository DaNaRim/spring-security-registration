package com.example.springsecurityregistration.service;

import com.example.springsecurityregistration.persistence.dao.RoleDao;
import com.example.springsecurityregistration.persistence.model.Role;
import com.example.springsecurityregistration.persistence.model.RoleName;
import com.example.springsecurityregistration.persistence.model.User;
import com.example.springsecurityregistration.web.error.UserHasNotRoleException;
import com.example.springsecurityregistration.web.error.UserHasRoleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleDao roleDao;
    private final UserService userService;

    @Autowired
    public RoleServiceImpl(RoleDao roleDao, UserService userService) {
        this.roleDao = roleDao;
        this.userService = userService;
    }

    @Override
    public void addRoleByUserId(long userId, RoleName roleName) {
        User user = userService.findById(userId);

        Set<Role> userRoles = user.getRoles();
        if (userRoles.contains(new Role(roleName))) {
            throw new UserHasRoleException("User already has this role");
        }
        userRoles.add(roleDao.findByRoleName(roleName));
        user.setRoles(userRoles);
    }

    @Override
    public void removeRoleByUserId(long userId, RoleName roleName) {
        User user = userService.findById(userId);

        Set<Role> userRoles = user.getRoles();
        if (!userRoles.contains(new Role(roleName))) {
            throw new UserHasNotRoleException("User has not this role");
        }
        userRoles.remove(roleDao.findByRoleName(roleName));
        user.setRoles(userRoles);
    }
}
