package com.example.springsecurityregistration.persistence.dao;

import com.example.springsecurityregistration.persistence.model.Role;
import com.example.springsecurityregistration.persistence.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleDao extends JpaRepository<Role, Long> {

    Role findByRoleName(RoleName roleName);
}
