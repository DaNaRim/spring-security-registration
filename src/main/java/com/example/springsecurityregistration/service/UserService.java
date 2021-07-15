package com.example.springsecurityregistration.service;

import com.example.springsecurityregistration.error.UserAlreadyExistException;
import com.example.springsecurityregistration.persistence.dao.UserRepository;
import com.example.springsecurityregistration.persistence.dao.VerificationTokenRepository;
import com.example.springsecurityregistration.persistence.model.User;
import com.example.springsecurityregistration.persistence.model.VerificationToken;
import com.example.springsecurityregistration.web.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       VerificationTokenRepository tokenRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerNewUserAccount(UserDto userDto) throws UserAlreadyExistException {

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
    public User getUserByToken(String verificationToken) {
        return tokenRepository.findByToken(verificationToken).getUser(); //TODO optimize
    }

    @Override
    public void saveRegisteredUser(User user) {
        userRepository.save(user);
    }

    @Override
    public VerificationToken getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    @Override
    public VerificationToken generateNewVerificationToken(String existingToken) {
        return null;
    }

    @Override
    public void createVerificationToken(User user, String token) {
        tokenRepository.save(new VerificationToken(token, user));
    }
}
