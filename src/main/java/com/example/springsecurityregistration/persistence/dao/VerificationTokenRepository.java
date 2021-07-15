package com.example.springsecurityregistration.persistence.dao;

import com.example.springsecurityregistration.persistence.model.User;
import com.example.springsecurityregistration.persistence.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);
}
