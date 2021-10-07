package com.example.springsecurityregistration.service;

import com.example.springsecurityregistration.error.InvalidTokenException;
import com.example.springsecurityregistration.persistence.dao.TokenRepository;
import com.example.springsecurityregistration.persistence.dao.UserRepository;
import com.example.springsecurityregistration.persistence.model.Token;
import com.example.springsecurityregistration.persistence.model.TokenType;
import com.example.springsecurityregistration.persistence.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Autowired
    public TokenServiceImpl(TokenRepository tokenRepository, UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    /*
        VerificationToken
     */

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
            throw new InvalidTokenException("tokenExpired");

        } else if (verificationToken.getUser().getId() != userId) {
            throw new InvalidTokenException("tokenForAnotherUser");

        } else if (verificationToken.getUser().isEnabled()) {
            throw new InvalidTokenException("userAlreadyEnable");
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

    /*
        PasswordResetToken
     */

    @Override
    public Token createPasswordResetToken(User user) {
        return tokenRepository.save(new Token(user, TokenType.PASSWORD_RESET));
    }

    @Override
    public void validatePasswordResetToken(long userId, String token) {
        Token passToken = tokenRepository.findByToken(token);

        if (passToken == null) {
            throw new InvalidTokenException("invalidToken");

        } else if (passToken.getUser().getId() != userId) {
            throw new InvalidTokenException("tokenForAnotherUser");

        } else if (passToken.isExpired()) {
            throw new InvalidTokenException("tokenExpired");
        }

        User user = passToken.getUser();
        Collection<GrantedAuthority> authorities =
                (Collection<GrantedAuthority>) userRepository.findByEmail(user.getEmail()).getAuthorities();
        //TODO sipmify
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
