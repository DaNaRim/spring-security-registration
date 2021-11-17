package com.example.springsecurityregistration.service;

import com.example.springsecurityregistration.persistence.dao.UserDao;
import com.example.springsecurityregistration.persistence.model.Token;
import com.example.springsecurityregistration.persistence.model.User;
import com.example.springsecurityregistration.web.dto.ForgotPasswordDto;
import com.example.springsecurityregistration.web.dto.RegistrationDto;
import com.example.springsecurityregistration.web.dto.UpdatePasswordDto;
import com.example.springsecurityregistration.web.error.InvalidOldPasswordException;
import com.example.springsecurityregistration.web.error.UserAlreadyExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserDao userDao,
                           TokenService tokenService,
                           PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerNewUserAccount(RegistrationDto registrationDto) {

        if (userDao.existsByEmail(registrationDto.getEmail())) {
            throw new UserAlreadyExistException("email is busy: " + registrationDto.getEmail());
        }

        User user = new User(
                registrationDto.getFirstName(),
                registrationDto.getLastName(),
                registrationDto.getEmail(),
                passwordEncoder.encode(registrationDto.getPassword()));

        return userDao.save(user);
    }

    @Override
    public void enableUser(String token) {
        Token verificationToken = tokenService.validateVerificationToken(token);
        User user = verificationToken.getUser();
        user.setEnabled(true);
    }

    @Override
    public User findUserByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public void changeUserPassword(long userId, UpdatePasswordDto passwordDto) {
        if (!passwordEncoder.matches(passwordDto.getOldPassword(), userDao.getPasswordById(userId))) {
            throw new InvalidOldPasswordException("Invalid old password");
        }
        User user = userDao.getById(userId);
        user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
    }

    @Override
    public void changeForgottenPassword(ForgotPasswordDto passwordDto) {
        Token token = tokenService.validatePasswordResetToken(passwordDto.getToken());
        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
    }
}
