package com.example.springsecurityregistration.persistence.dao;

import com.example.springsecurityregistration.persistence.model.Token;
import com.example.springsecurityregistration.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Token findByToken(String token);

    Token findByUser(User user);
}
