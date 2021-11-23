package com.example.springsecurityregistration.web.error;

public class UserHasNotRoleException extends RuntimeException {

    public UserHasNotRoleException(String message) {
        super(message);
    }
}
