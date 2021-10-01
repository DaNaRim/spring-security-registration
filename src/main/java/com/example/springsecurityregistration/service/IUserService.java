package com.example.springsecurityregistration.service;

import com.example.springsecurityregistration.persistence.model.Token;
import com.example.springsecurityregistration.persistence.model.User;
import com.example.springsecurityregistration.web.dto.UserDto;

public interface IUserService {

    User registerNewUserAccount(UserDto userDto);

    void enableUser(long userId);

    User findUserByEmail(String email);

    User findUserByToken(String verificationToken);

    void saveRegisteredUser(User user);

    Token createVerificationToken(User user);

    void validateVerificationToken(long userId, String token);

    Token getVerificationToken(String verificationToken);

    Token generateNewVerificationToken(String existingToken);

    Token createPasswordResetToken(User user);

    void validatePasswordResetToken(long userId, String token);

    void validateOldPassword(long id, String oldPassword);

    void changeUserPassword(long id, String password);

}
