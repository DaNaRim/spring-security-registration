package com.example.springsecurityregistration.service;

import com.example.springsecurityregistration.error.UserAlreadyExistException;
import com.example.springsecurityregistration.persistence.model.User;
import com.example.springsecurityregistration.persistence.model.VerificationToken;
import com.example.springsecurityregistration.web.dto.UserDto;

public interface IUserService {

    User registerNewUserAccount(UserDto userDto) throws UserAlreadyExistException;

    User getUserByToken(String verificationToken);

    void saveRegisteredUser(User user); //TODO optimize

    void createVerificationToken(User user, String token);

    VerificationToken generateNewVerificationToken(String existingToken);

    VerificationToken getVerificationToken(String VerificationToken);
}
