package com.example.springsecurityregistration.service;

import com.example.springsecurityregistration.persistence.model.Token;
import com.example.springsecurityregistration.persistence.model.User;

public interface TokenService {

    Token createVerificationToken(User user);

    Token validateVerificationToken(String token);

    Token getVerificationToken(String verificationToken);

    Token createPasswordResetToken(User user);

    Token validatePasswordResetToken(String token);

}
