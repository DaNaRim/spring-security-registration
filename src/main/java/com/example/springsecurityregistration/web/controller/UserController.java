package com.example.springsecurityregistration.web.controller;

import com.example.springsecurityregistration.error.InvalidTokenException;
import com.example.springsecurityregistration.event.OnRegistrationCompleteEvent;
import com.example.springsecurityregistration.persistence.model.User;
import com.example.springsecurityregistration.service.TokenEmailFacade;
import com.example.springsecurityregistration.service.TokenService;
import com.example.springsecurityregistration.service.UserService;
import com.example.springsecurityregistration.web.dto.ForgotPasswordDto;
import com.example.springsecurityregistration.web.dto.RegistrationDto;
import com.example.springsecurityregistration.web.dto.UpdatePasswordDto;
import com.example.springsecurityregistration.web.util.AuthorizationUtil;
import com.example.springsecurityregistration.web.util.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Locale;

@Controller
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;
    private final TokenEmailFacade tokenEmailFacade;
    private final ApplicationEventPublisher eventPublisher;
    private final MessageSource messages;

    @Autowired
    public UserController(UserService userService,
                          TokenService tokenService,
                          TokenEmailFacade tokenEmailFacade,
                          ApplicationEventPublisher eventPublisher,
                          MessageSource messages) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.tokenEmailFacade = tokenEmailFacade;
        this.eventPublisher = eventPublisher;
        this.messages = messages;
    }

    @PostMapping("/user/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    GenericResponse registerUserAccount(@RequestBody @Valid RegistrationDto registrationDto,
                                        HttpServletRequest request) {

        User user = userService.registerNewUserAccount(registrationDto);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request));
        return new GenericResponse("success");
    }

    @PutMapping("/registrationConfirm")
    public String confirmRegistration(@RequestParam("token") String token,
                                      Locale locale,
                                      Model model) {

        try {
            userService.enableUser(AuthorizationUtil.getUserId(), token);
        } catch (InvalidTokenException e) {
            String message = messages.getMessage("auth.message." + e.getMessage(), null, locale);
            model.addAttribute("message", message);
            return "redirect:/badUser.html?lang=" + locale.getLanguage();
        }

        model.addAttribute("message",
                messages.getMessage("message.accountVerified", null, locale));
        return "redirect:/login.html?lang=" + locale.getLanguage();
    }

    @GetMapping("/user/resendRegistrationToken")
    public String resendRegistrationToken(@RequestParam("token") String existingToken,
                                          HttpServletRequest request,
                                          Locale locale,
                                          Model model) {

        try {
            tokenEmailFacade.updateAndSendVerificationToken(existingToken, request);

        } catch (MailAuthenticationException e) {
            return "redirect:/emailError.html?lang=" + locale.getLanguage();
        } catch (Exception e) {
            model.addAttribute("message", e.getLocalizedMessage());
            return "redirect:/login.html?lang=" + locale.getLanguage();
        }
        model.addAttribute("message", messages.getMessage("message.resendToken", null, locale));
        return "redirect:/login.html?lang=" + locale.getLanguage();
    }

    @PostMapping("/user/resetPassword")
    public @ResponseBody
    GenericResponse resetPassword(@RequestParam("email") String userEmail,
                                  HttpServletRequest request) {

        tokenEmailFacade.createAndSendPasswordResetToken(userEmail, request);
        return new GenericResponse(
                messages.getMessage("message.resetPasswordEmail", null, request.getLocale()));
    }

    @GetMapping("/user/changePassword")
    public String showChangePasswordPage(@RequestParam("token") String token,
                                         Locale locale,
                                         Model model) {

        try {
            tokenService.validatePasswordResetToken(AuthorizationUtil.getUserId(), token);
        } catch (InvalidTokenException e) {
            String message = messages.getMessage("auth.message." + e.getMessage(), null, locale);
            return "redirect:/login.html?lang=" + locale.getLanguage() + "&message=" + message;
        }
        model.addAttribute("token", token);
        return "redirect:/updatePassword.html?lang=" + locale.getLanguage();
    }

    @PostMapping("/user/savePassword")
    public @ResponseBody
    GenericResponse savePassword(@RequestBody @Valid UpdatePasswordDto updatePasswordDto, //TODO permissions
                                 Locale locale) {

        userService.changeUserPassword(AuthorizationUtil.getUserId(), updatePasswordDto);
        return new GenericResponse(messages.getMessage("message.resetPasswordSuc", null, locale));
    }

    @PostMapping("/user/updatePassword")
    public @ResponseBody
    GenericResponse updatePassword(@RequestBody @Valid UpdatePasswordDto updatePasswordDto,
                                   Locale locale) {

        userService.changeUserPassword(AuthorizationUtil.getUserId(), updatePasswordDto);
        return new GenericResponse(messages.getMessage("message.updatePasswordSuc", null, locale));
    }

    @PostMapping("/user/updateForgottenPassword")
    public GenericResponse updateForgottenPassword(@RequestBody @Valid ForgotPasswordDto passwordDto,
                                                   Locale locale) {

        userService.changeForgottenPassword(AuthorizationUtil.getUserId(), passwordDto);
        return new GenericResponse(messages.getMessage("message.updatePasswordSuc", null, locale));
    }

}
