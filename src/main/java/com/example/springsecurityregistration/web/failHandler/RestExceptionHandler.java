package com.example.springsecurityregistration.web.failHandler;

import com.example.springsecurityregistration.web.error.InvalidTokenException;
import com.example.springsecurityregistration.web.error.UnauthorizedException;
import com.example.springsecurityregistration.web.error.UserAlreadyExistException;
import com.example.springsecurityregistration.web.error.UserNotFoundException;
import com.example.springsecurityregistration.web.util.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messages;

    @Autowired
    public RestExceptionHandler(MessageSource messages) {
        this.messages = messages;
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException e,
                                                         HttpHeaders headers,
                                                         HttpStatus status,
                                                         WebRequest request) {
        BindingResult result = e.getBindingResult();
        GenericResponse bodyOfResponse = new GenericResponse(result.getFieldErrors(), result.getGlobalErrors());
        return handleExceptionInternal(e, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({InvalidTokenException.class})
    public ResponseEntity<Object> handleToken(RuntimeException e, WebRequest request) {
        logger.warn("HTTP 400: InvalidToken " + e.getMessage());

        GenericResponse responseBody = new GenericResponse(
                messages.getMessage("error.token." + e.getMessage(), null, request.getLocale()));

        return handleExceptionInternal(e, responseBody, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<Object> handleUnauthorized(RuntimeException e, WebRequest request) {
        logger.warn("HTTP 401: InvalidToken " + e.getMessage());

        GenericResponse responseBody = new GenericResponse(
                messages.getMessage("error.user.unauthorized", null, request.getLocale()));

        return handleExceptionInternal(e, responseBody, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<Object> handleUserNotFound(RuntimeException e, WebRequest request) {
        logger.warn("HTTP 404: UserNotFound " + e.getMessage());
        GenericResponse responseBody = new GenericResponse(
                messages.getMessage("error.user.notFound", null, request.getLocale()), "UserNotFound");

        return handleExceptionInternal(e, responseBody, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({UserAlreadyExistException.class})
    public ResponseEntity<Object> handleUserAlreadyExist(RuntimeException e, WebRequest request) {
        logger.warn("HTTP 409: UserAlreadyExist " + e.getMessage());
        GenericResponse bodyOfResponse = new GenericResponse(
                messages.getMessage("error.busyEmail", null, request.getLocale()), "UserAlreadyExist");

        return handleExceptionInternal(e, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler({MailAuthenticationException.class})
    public ResponseEntity<Object> handleMail(RuntimeException e, WebRequest request) {
        logger.error("HTTP 500: MailAuthenticationException " + e.getMessage());
        GenericResponse responseBody = new GenericResponse(
                messages.getMessage("error.email.config.error", null, request.getLocale()), "MailError");

        return handleExceptionInternal(e, responseBody, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleInternal(RuntimeException e, WebRequest request) {
        logger.error("HTTP 500: Internal server error ", e);
        GenericResponse responseBody = new GenericResponse(
                messages.getMessage("error.internalServer", null, request.getLocale()), "InternalServerError");

        return handleExceptionInternal(e, responseBody, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

}
