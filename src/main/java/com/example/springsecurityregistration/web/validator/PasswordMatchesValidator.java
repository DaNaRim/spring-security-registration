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

        if (obj instanceof RegistrationDto) {
            RegistrationDto user = (RegistrationDto) obj;
            return user.getPassword().equals(user.getMatchingPassword());

        } else if (obj instanceof UpdatePasswordDto) {
            UpdatePasswordDto password = (UpdatePasswordDto) obj;
            return password.getNewPassword().equals(password.getMatchingPassword());

        } else if (obj instanceof ForgotPasswordDto) {
            ForgotPasswordDto password = (ForgotPasswordDto) obj;
            return password.getNewPassword().equals(password.getMatchingPassword());

        } else {
            logger.error("obj is not a valid type");
            throw new RuntimeException("obj is not a valid type");
        }
    }
}
