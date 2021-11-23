package com.example.springsecurityregistration.service;

import com.example.springsecurityregistration.persistence.model.User;
import com.example.springsecurityregistration.web.dto.ForgotPasswordDto;
import com.example.springsecurityregistration.web.dto.RegistrationDto;
import com.example.springsecurityregistration.web.dto.UpdatePasswordDto;

public interface UserService {

    User registerNewUserAccount(RegistrationDto registrationDto);

    void enableUser(String token);

    User findById(long id);

    User findByEmail(String email);

    void changeForgottenPassword(ForgotPasswordDto passwordDto);

    void changeUserPassword(long userId, UpdatePasswordDto passwordDto);

}
