package com.example.springsecurityregistration.web.util;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Collectors;

public class GenericResponse {

    private String message;
    private String error;

    public GenericResponse(final String message) {
        this.message = message;
    }

    public GenericResponse(final String message, final String error) {
        this.message = message;
        this.error = error;
    }

    public GenericResponse(List<ObjectError> allErrors, String error) {
        this.error = error;
        String message = allErrors.stream().map(err -> {
            if (err instanceof FieldError) {
                return String.format("{\"field\":\"%s\",\"defaultMessage\":\"%s\"}", //JSON format
                        ((FieldError) err).getField(), err.getDefaultMessage());
            } else {
                return String.format("{\"object\":\"%s\",\"defaultMessage\":\"%s\"}",
                        err.getObjectName(), err.getDefaultMessage());
            }
        }).collect(Collectors.joining(","));
        this.message = "[" + message + "]";
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
