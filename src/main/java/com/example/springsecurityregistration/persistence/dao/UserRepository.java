package com.example.springsecurityregistration.persistence.dao;

import com.example.springsecurityregistration.persistence.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    User findByEmail(String email);

    boolean existsByEmail(String email);
}
