package com.example.springsecurityregistration.web.controller;

import com.example.springsecurityregistration.error.InvalidTokenException;
import com.example.springsecurityregistration.event.OnRegistrationCompleteEvent;
import com.example.springsecurityregistration.persistence.model.Token;
import com.example.springsecurityregistration.persistence.model.User;
import com.example.springsecurityregistration.service.IUserService;
import com.example.springsecurityregistration.web.dto.PasswordDto;
import com.example.springsecurityregistration.web.dto.UserDto;
import com.example.springsecurityregistration.web.util.AuthorizationUtil;
import com.example.springsecurityregistration.web.util.GenericResponse;
import com.example.springsecurityregistration.web.util.MailUtil;
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

    private final IUserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final MessageSource messages;
    private final MailUtil mailUtil;

    @Autowired
    public UserController(IUserService userService,
                          ApplicationEventPublisher eventPublisher,
                          MessageSource messages,
                          MailUtil mailUtil) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
        this.messages = messages;
        this.mailUtil = mailUtil;
    }

    @PostMapping("/user/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    GenericResponse registerUserAccount(@RequestBody @Valid UserDto userDto,
                                        HttpServletRequest request) {

        User registered = userService.registerNewUserAccount(userDto);
        eventPublisher.publishEvent(
                new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request)));
        return new GenericResponse("success");
    }

    @PutMapping("/registrationConfirm")
    public String confirmRegistration(@RequestParam("token") String token,
                                      Locale locale,
                                      Model model) {

        long id = AuthorizationUtil.getUserId();
        try {
            userService.validateVerificationToken(AuthorizationUtil.getUserId(), token);
        } catch (InvalidTokenException e) {
            String message = messages.getMessage("auth.message." + e.getMessage(), null, locale);
            model.addAttribute("message", message);
            return "redirect:/badUser.html?lang=" + locale.getLanguage();
        }

        userService.enableUser(id);

        model.addAttribute("message",
                messages.getMessage("message.accountVerified", null, locale));
        return "redirect:/login.html?lang=" + locale.getLanguage();
    }

    @GetMapping("/user/resendRegistrationToken")
    public String resendRegistrationToken(@RequestParam("token") String existingToken,
                                          HttpServletRequest request,
                                          Locale locale,
                                          Model model) {

        Token newToken = userService.generateNewVerificationToken(existingToken);
        User user = newToken.getUser();
        try {
            mailUtil.sendVerificationTokenEmail(getAppUrl(request), locale, newToken, user);

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
                                  HttpServletRequest request) { //TODO transactional

        User user = userService.findUserByEmail(userEmail);
        String token = userService.createPasswordResetToken(user).getToken();
        mailUtil.sendResetPasswordTokenEmail(getAppUrl(request), request.getLocale(), token, user);
        return new GenericResponse(
                messages.getMessage("message.resetPasswordEmail", null, request.getLocale()));
    }

    @GetMapping("/user/changePassword")
    public String showChangePasswordPage(@RequestParam("token") String token,
                                         Locale locale,
                                         Model model) {

        try {
            userService.validatePasswordResetToken(AuthorizationUtil.getUserId(), token);
        } catch (InvalidTokenException e) {
            String message = messages.getMessage("auth.message." + e.getMessage(), null, locale);
            return "redirect:/login.html?lang=" + locale.getLanguage() + "&message=" + message;
        }
        model.addAttribute("token", token);
        return "redirect:/updatePassword.html?lang=" + locale.getLanguage();
    }

    @PostMapping("/user/savePassword")
    public @ResponseBody
    GenericResponse savePassword(@RequestBody @Valid PasswordDto passwordDto,
                                 Locale locale) {

        userService.changeUserPassword(AuthorizationUtil.getUserId(), passwordDto.getNewPassword());
        return new GenericResponse(messages.getMessage("message.resetPasswordSuc", null, locale));
    }

    @PostMapping("/user/updatePassword")
    public @ResponseBody
    GenericResponse changeUserPassword(@RequestBody @Valid PasswordDto passwordDto,
                                       Locale locale) {
        long id = AuthorizationUtil.getUserId();

        userService.validateOldPassword(id, passwordDto.getOldPassword());
        userService.changeUserPassword(id, passwordDto.getNewPassword());

        return new GenericResponse(messages.getMessage("message.updatePasswordSuc", null, locale));
    }

    private String getAppUrl(HttpServletRequest request) {
        return String.format("http://%s:%d%s",
                request.getServerName(), request.getServerPort(), request.getContextPath());
    }
}
