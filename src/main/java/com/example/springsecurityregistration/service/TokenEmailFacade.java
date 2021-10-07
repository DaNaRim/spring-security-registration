package com.example.springsecurityregistration.service;

import com.example.springsecurityregistration.persistence.model.User;

import javax.servlet.http.HttpServletRequest;

public interface TokenEmailFacade {

    void createAndSendVerificationToken(User user, HttpServletRequest request);

    void updateAndSendVerificationToken(String existingToken, HttpServletRequest request);

    void createAndSendPasswordResetToken(String userEmail, HttpServletRequest request);
}
