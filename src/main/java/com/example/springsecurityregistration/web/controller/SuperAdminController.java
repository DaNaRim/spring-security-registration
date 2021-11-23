package com.example.springsecurityregistration.web.controller;

import com.example.springsecurityregistration.persistence.model.RoleName;
import com.example.springsecurityregistration.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/superAdmin")
public class SuperAdminController {

    private final RoleService roleService;

    @Autowired
    public SuperAdminController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PutMapping("/addRole")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void makeAdmin(@RequestParam Long userId,
                          @RequestParam RoleName roleName) {

        roleService.addRoleByUserId(userId, roleName);
    }

    @PutMapping("/removeRole")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAdmin(@RequestParam Long userId,
                            @RequestParam RoleName roleName) {

        roleService.removeRoleByUserId(userId, roleName);
    }
}