package com.example.springsecurityregistration.service;

import com.example.springsecurityregistration.persistence.model.Token;
import com.example.springsecurityregistration.persistence.model.User;

public interface TokenService {

    Token createVerificationToken(User user);

    void validateVerificationToken(long userId, String token);

    Token getVerificationToken(String verificationToken);

    Token generateNewVerificationToken(String existingToken);

    Token createPasswordResetToken(User user);

    void validatePasswordResetToken(long userId, String token);

}
