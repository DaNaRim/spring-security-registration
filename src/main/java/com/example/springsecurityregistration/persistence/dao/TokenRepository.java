package com.example.springsecurityregistration.persistence.dao;

import com.example.springsecurityregistration.persistence.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Token findByToken(String token);
}
