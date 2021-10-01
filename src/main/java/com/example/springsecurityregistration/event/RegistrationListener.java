package com.example.springsecurityregistration.event;

import com.example.springsecurityregistration.persistence.model.User;
import com.example.springsecurityregistration.service.IUserService;
import com.example.springsecurityregistration.web.util.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final IUserService iUserService;
    private final MessageSource messages;
    private final MailUtil mailUtil;

    @Autowired
    public RegistrationListener(IUserService iUserService,
                                MessageSource messages,
                                MailUtil mailUtil) {
        this.iUserService = iUserService;
        this.messages = messages;
        this.mailUtil = mailUtil;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent onRegistrationCompleteEvent) {
        this.confirmRegistration(onRegistrationCompleteEvent);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = iUserService.createVerificationToken(user).getToken();

        String confirmationUrl = event.getAppUrl() + "/regitrationConfirm.html?token=" + token;
        String message = messages.getMessage("message.regSucc", null, event.getLocale());

        mailUtil.constructAndSendEmail(
                "Registration Confirmation",
                String.format("%s\r\nhttp://localhost:8080%s", message, confirmationUrl),
                user
        );
    }
}
