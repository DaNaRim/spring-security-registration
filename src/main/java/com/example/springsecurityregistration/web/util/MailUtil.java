package com.example.springsecurityregistration.web.util;

import com.example.springsecurityregistration.persistence.model.Token;
import com.example.springsecurityregistration.persistence.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Objects;

@Component
public class MailUtil {

    private final Environment environment;
    private final MessageSource messages;
    private final JavaMailSender mailSender;

    @Autowired
    public MailUtil(Environment environment, MessageSource messages, JavaMailSender mailSender) {
        this.environment = environment;
        this.messages = messages;
        this.mailSender = mailSender;
    }

    public void sendVerificationTokenEmail(String contextPath,
                                           Locale locale,
                                           Token token,
                                           User user) {

        String url = String.format("%s/registrationConfirm.html?token=%s", contextPath, token.getToken());
        String message = messages.getMessage("message.resendToken", null, locale);
        mailSender.send(constructEmail("Resend Registration Token", message + "\r\n" + url, user));
    }

    public void sendResetPasswordTokenEmail(String contextPath,
                                            Locale locale,
                                            String token,
                                            User user) {

        String url = String.format("%s/user/changePassword?id=%d&token=%s", contextPath, user.getId(), token);
        String message = messages.getMessage("message.resetPassword", null, locale);
        mailSender.send(constructEmail("Reset Password", message + "\r\n" + url, user));
    }

    public void constructAndSendEmail(String subject, String body, User user) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(Objects.requireNonNull(environment.getProperty("support.email")));
        mailSender.send(email);
    }

    private SimpleMailMessage constructEmail(String subject, String body, User user) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(Objects.requireNonNull(environment.getProperty("support.email")));
        return email;
    }
}
