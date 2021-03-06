package com.example.springsecurityregistration.service;

import com.example.springsecurityregistration.persistence.model.Token;
import com.example.springsecurityregistration.persistence.model.User;
import com.example.springsecurityregistration.web.error.InvalidTokenException;
import com.example.springsecurityregistration.web.util.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
@Transactional
public class TokenEmailFacadeImpl implements TokenEmailFacade {

    private final UserService userService;
    private final TokenService tokenService;
    private final MailUtil mailUtil;

    @Autowired
    public TokenEmailFacadeImpl(UserService userService,
                                TokenService tokenService,
                                MailUtil mailUtil) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.mailUtil = mailUtil;
    }

    @Override
    public void createAndSendVerificationToken(User user, HttpServletRequest request) {
        Token token = tokenService.createVerificationToken(user);

        mailUtil.sendVerificationTokenEmail(
                getAppUrl(request),
                request.getLocale(),
                token.getToken(),
                user.getEmail());
    }

    @Override
    public void updateAndSendVerificationToken(String userEmail, HttpServletRequest request) {
        User user = userService.findByEmail(userEmail);

        if (user.isEnabled()) {
            throw new InvalidTokenException("userAlreadyEnable");
        }
        Token token = tokenService.createVerificationToken(user);

        mailUtil.sendVerificationTokenEmail(
                getAppUrl(request),
                request.getLocale(),
                token.getToken(),
                userEmail);
    }

    @Override
    public void createAndSendPasswordResetToken(String userEmail, HttpServletRequest request) {
        User user = userService.findByEmail(userEmail);
        Token token = tokenService.createPasswordResetToken(user);

        mailUtil.sendResetPasswordTokenEmail(
                getAppUrl(request),
                request.getLocale(),
                token.getToken(),
                user.getEmail());
    }

    private String getAppUrl(HttpServletRequest request) {
        return String.format("http://%s:%d%s",
                request.getServerName(), request.getServerPort(), request.getContextPath());
    }
}
