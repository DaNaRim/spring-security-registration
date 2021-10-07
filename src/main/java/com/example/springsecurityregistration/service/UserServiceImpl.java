package com.example.springsecurityregistration.service;

import com.example.springsecurityregistration.error.InvalidOldPasswordException;
import com.example.springsecurityregistration.error.UserAlreadyExistException;
import com.example.springsecurityregistration.persistence.dao.UserRepository;
import com.example.springsecurityregistration.persistence.model.User;
import com.example.springsecurityregistration.web.dto.ForgotPasswordDto;
import com.example.springsecurityregistration.web.dto.RegistrationDto;
import com.example.springsecurityregistration.web.dto.UpdatePasswordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           TokenService tokenService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerNewUserAccount(RegistrationDto registrationDto) {

        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new UserAlreadyExistException("There is an account with that email address: " + registrationDto.getEmail());
        }

        User user = new User(
                registrationDto.getFirstName(),
                registrationDto.getLastName(),
                passwordEncoder.encode(registrationDto.getPassword()),
                registrationDto.getEmail());

        return userRepository.save(user);
    }

    @Override
    public void enableUser(long userId, String token) {
        tokenService.validateVerificationToken(userId, token);
        userRepository.updateIsEnable(userId);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void changeUserPassword(long userId, UpdatePasswordDto passwordDto) {
        validateOldPassword(userId, passwordDto.getOldPassword());
        userRepository.updatePassword(userId, passwordEncoder.encode(passwordDto.getNewPassword()));
    }

    @Override
    public void changeForgottenPassword(long userId, ForgotPasswordDto passwordDto) {
        tokenService.validatePasswordResetToken(userId, passwordDto.getToken());
        userRepository.updatePassword(userId, passwordEncoder.encode(passwordDto.getNewPassword()));
    }

    private void validateOldPassword(long id, String oldPassword) {
        if (!oldPassword.equals(userRepository.getPasswordById(id))) {
            throw new InvalidOldPasswordException("password Invalid");
        }
    }

}
