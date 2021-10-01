package com.example.springsecurityregistration.web.dto;

import com.example.springsecurityregistration.web.validator.ValidPassword;

import javax.validation.constraints.NotBlank;

public class PasswordDto {

    @NotBlank
    private String oldPassword;

    @NotBlank
    private String token;

    @ValidPassword
    @NotBlank
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
