package com.example.springsecurityregistration.service;

import com.example.springsecurityregistration.error.InvalidOldPasswordException;
import com.example.springsecurityregistration.error.InvalidTokenException;
import com.example.springsecurityregistration.error.UserAlreadyExistException;
import com.example.springsecurityregistration.persistence.dao.TokenRepository;
import com.example.springsecurityregistration.persistence.dao.UserRepository;
import com.example.springsecurityregistration.persistence.model.Token;
import com.example.springsecurityregistration.persistence.model.TokenType;
import com.example.springsecurityregistration.persistence.model.User;
import com.example.springsecurityregistration.web.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       TokenRepository tokenRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerNewUserAccount(UserDto userDto) {

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistException("There is an account with that email address: " + userDto.getEmail());
        }

        User user = new User(
                userDto.getFirstName(),
                userDto.getLastName(),
                passwordEncoder.encode(userDto.getPassword()),
                userDto.getEmail());

        return userRepository.save(user);
    }

    @Override
    public void enableUser(long userId) {
        userRepository.updateIsEnable(userId);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findUserByToken(String verificationToken) {
        return userRepository.findUserByToken(verificationToken);
    }

    @Override
    public void saveRegisteredUser(User user) {
        userRepository.save(user);
    }

    @Override
    public Token createVerificationToken(User user) {
        return tokenRepository.save(new Token(user, TokenType.VERIFICATION));
    }

    @Override
    public void validateVerificationToken(long userId, String token) {
        Token verificationToken = getVerificationToken(token);

        if (verificationToken == null) {
            throw new InvalidTokenException("invalidToken");

        } else if (verificationToken.isExpired()) {
            throw new InvalidTokenException("expired");

        } else if (verificationToken.getUser().isEnabled()) {
            throw new InvalidTokenException("enable");
        }
    }

    @Override
    public Token getVerificationToken(String verificationToken) {
        return tokenRepository.findByToken(verificationToken);
    }

    @Override
    public Token generateNewVerificationToken(String existingToken) {
        Token token = tokenRepository.findByToken(existingToken);
        token.updateToken(); //TODO check update in db
        return token;
    }

    @Override
    public Token createPasswordResetToken(User user) {
        return tokenRepository.save(new Token(user, TokenType.PASSWORD_RESET));
    }

    @Override
    public void validatePasswordResetToken(long userId, String token) {
        Token passToken = tokenRepository.findByToken(token);

        if (passToken == null || passToken.getUser().getId() != userId) {
            throw new InvalidTokenException("invalidToken");

        } else if (passToken.isExpired()) {
            throw new InvalidTokenException("expired");
        }

        User user = passToken.getUser();
        Collection<GrantedAuthority> authorities =
                (Collection<GrantedAuthority>) userRepository.findByEmail(user.getEmail()).getAuthorities();
        //TODO sipmify
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Override
    public void validateOldPassword(long id, String oldPassword) {
        if (!oldPassword.equals(userRepository.getPasswordById(id))) {
            throw new InvalidOldPasswordException("password Invalid");
        }
    }

    @Override
    public void changeUserPassword(long id, String password) {
        userRepository.updatePassword(id, passwordEncoder.encode(password));
    }
}
