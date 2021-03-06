package com.example.springsecurityregistration.web.dto;

import com.example.springsecurityregistration.web.validator.PasswordMatches;
import com.example.springsecurityregistration.web.validator.ValidPassword;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@PasswordMatches
public class ForgotPasswordDto extends PasswordEntity {

    @ValidPassword
    @NotBlank
    @Size(min = 8, max = 30)
    private String newPassword;

    @NotBlank
    private String matchingPassword;

    @NotBlank
    private String token;

    @Override
    public String getPassword() {
        return newPassword;
    }

    @Override
    public String getMatchingPassword() {
        return matchingPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
