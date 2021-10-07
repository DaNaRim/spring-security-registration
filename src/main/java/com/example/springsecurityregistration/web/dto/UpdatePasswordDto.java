package com.example.springsecurityregistration.web.dto;

import com.example.springsecurityregistration.web.validator.PasswordMatches;
import com.example.springsecurityregistration.web.validator.ValidPassword;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@PasswordMatches
public class UpdatePasswordDto {

    @NotBlank
    private String oldPassword;

    @ValidPassword
    @NotBlank
    @Size(min = 8, max = 30)
    private String newPassword;

    @NotBlank
    private String matchingPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }
}
