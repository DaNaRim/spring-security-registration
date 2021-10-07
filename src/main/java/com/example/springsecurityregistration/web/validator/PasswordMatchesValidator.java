package com.example.springsecurityregistration.web.validator;

import com.example.springsecurityregistration.web.dto.ForgotPasswordDto;
import com.example.springsecurityregistration.web.dto.RegistrationDto;
import com.example.springsecurityregistration.web.dto.UpdatePasswordDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    private final Log logger = LogFactory.getLog(PasswordMatchesValidator.class);

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        try {
            RegistrationDto user = (RegistrationDto) obj;
            return user.getPassword().equals(user.getMatchingPassword());
        } catch (ClassCastException e) {
            logger.trace("obj param is not RegistrationDto type");
        }

        try {
            UpdatePasswordDto password = (UpdatePasswordDto) obj;
            return password.getNewPassword().equals(password.getMatchingPassword());
        } catch (ClassCastException e) {
            logger.trace("obj param is not PasswordDto type");
        }

        try {
            ForgotPasswordDto password = (ForgotPasswordDto) obj;
            return password.getNewPassword().equals(password.getMatchingPassword());
        } catch (ClassCastException e) {
            logger.trace("obj param is not ForgottenPasswordDto type");
        }

        throw new RuntimeException("obj is not a valid type");
    }
}
