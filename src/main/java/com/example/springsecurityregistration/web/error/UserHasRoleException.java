package com.example.springsecurityregistration.web.error;

public class UserHasRoleException extends RuntimeException {

    public UserHasRoleException(String message) {
        super(message);
    }
}
