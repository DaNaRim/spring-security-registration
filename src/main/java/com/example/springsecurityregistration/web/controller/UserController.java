package com.example.springsecurityregistration.web.controller;

import com.example.springsecurityregistration.error.UserAlreadyExistException;
import com.example.springsecurityregistration.event.OnRegistrationCompleteEvent;
import com.example.springsecurityregistration.persistence.model.User;
import com.example.springsecurityregistration.persistence.model.VerificationToken;
import com.example.springsecurityregistration.service.IUserService;
import com.example.springsecurityregistration.web.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Locale;
import java.util.Objects;

@Controller
public class UserController {

    private final IUserService userService;
    private final UserDetailsService userDetailsService;
    private final ApplicationEventPublisher eventPublisher;
    private final JavaMailSender mailSender;
    private final MessageSource messages;
    private final Environment environment;

    @Autowired
    public UserController(IUserService userService,
                          UserDetailsService userDetailsService,
                          ApplicationEventPublisher eventPublisher,
                          JavaMailSender mailSender,
                          MessageSource messages,
                          Environment environment) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.eventPublisher = eventPublisher;
        this.mailSender = mailSender;
        this.messages = messages;
        this.environment = environment;
    }

    @GetMapping("/user/registration")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "registration";
    }

    @PostMapping("/user/registration")
    public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid UserDto userDto,
                                            HttpServletRequest request) {
        try {
            User registered = userService.registerNewUserAccount(userDto);

            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(
                    registered, request.getLocale(), request.getContextPath()));

        } catch (UserAlreadyExistException e) {
            ModelAndView mav = new ModelAndView("registration", "user", userDto);
            mav.addObject("message", "An account for that username/email already exists.");
            return mav;
        } catch (RuntimeException e) {
            return new ModelAndView("emailError", "user", userDto);
        }
        return new ModelAndView("successRegister", "user", userDto);
    }

    @GetMapping("/registrationConfirm")
    public String confirmRegistration(@RequestParam("token") String token,
                                      Locale locale,
                                      Model model) {

        VerificationToken verificationToken = userService.getVerificationToken(token);

        if (verificationToken == null) {
            String message = messages.getMessage("auth.message.invalidToken", null, locale);
            model.addAttribute("message", message);
            return "redirect:/badUser.html?lang=" + locale.getLanguage();

        } else if (verificationToken.isExpired()) {
            String messageValue = messages.getMessage("auth.message.expired", null, locale);
            model.addAttribute("message", messageValue);
            return "redirect:/badUser.html?lang=" + locale.getLanguage();
        }
        //TODO already verified

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userService.saveRegisteredUser(user);

        model.addAttribute("message", messages.getMessage("message.accountVerified", null, locale));
        return "redirect:/login.html?lang=" + locale.getLanguage();
    }

    @GetMapping("/user/resendRegistrationToken")
    public String resendRegistrationToken(@RequestParam("token") String existingToken,
                                          HttpServletRequest request,
                                          Locale locale,
                                          Model model) {

        final VerificationToken newToken = userService.generateNewVerificationToken(existingToken);
        final User user = userService.getUserByToken(newToken.getToken()); //TODO optimize
        try {
            final String appUrl = String.format("http://%s:%d%s",
                    request.getServerName(), request.getServerPort(), request.getContextPath());

            final SimpleMailMessage email = constructResetVerificationTokenEmail(appUrl, locale, newToken, user);

            mailSender.send(email);
        } catch (final MailAuthenticationException e) {
            return "redirect:/emailError.html?lang=" + locale.getLanguage();
        } catch (final Exception e) {
            model.addAttribute("message", e.getLocalizedMessage());
            return "redirect:/login.html?lang=" + locale.getLanguage();
        }
        model.addAttribute("message", messages.getMessage("message.resendToken", null, locale));
        return "redirect:/login.html?lang=" + locale.getLanguage();
    }

    private SimpleMailMessage constructResetVerificationTokenEmail(String contextPath,
                                                                   Locale locale,
                                                                   VerificationToken newToken,
                                                                   User user) {

        String message = messages.getMessage("message.resendToken", null, locale);
        String confirmationUrl = String.format("%s/registrationConfirm.html?token=%s",
                contextPath, newToken.getToken());

        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject("Resend Registration Token");
        email.setText(String.format("%s\r\n%s", message, confirmationUrl));
        email.setFrom(Objects.requireNonNull(environment.getProperty("support.email")));
        email.setTo(user.getEmail());
        return email;
    }
}
