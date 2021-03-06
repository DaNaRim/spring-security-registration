package com.example.springsecurityregistration.service.event;

import com.example.springsecurityregistration.service.TokenEmailFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final TokenEmailFacade tokenEmailFacade;

    @Autowired
    public RegistrationListener(TokenEmailFacade tokenEmailFacade) {
        this.tokenEmailFacade = tokenEmailFacade;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent onRegistrationCompleteEvent) {
        this.confirmRegistration(onRegistrationCompleteEvent);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        tokenEmailFacade.createAndSendVerificationToken(event.getUser(), event.getRequest());
    }
}
