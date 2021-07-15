package com.example.springsecurityregistration.web.dto;

import com.example.springsecurityregistration.web.validator.PasswordMatches;
import com.example.springsecurityregistration.web.validator.ValidEmail;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@PasswordMatches
public class UserDto {

    @NotBlank
    @Size(min = 2, max = 35)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 35)
    private String lastName;

    @NotBlank
    @Size(min = 8, max = 70)
    private String password;
    private String matchingPassword;

    @ValidEmail
    @NotBlank
    private String email; //max 62

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
