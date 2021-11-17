package com.example.springsecurityregistration.persistence.dao;

import com.example.springsecurityregistration.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User, Long> {

    User findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(value = "SELECT password FROM users WHERE id = :id",
            nativeQuery = true)
    String getPasswordById(@Param("id") long id);

}
