package com.example.springsecurityregistration.web.failHandler;

import com.example.springsecurityregistration.error.UserAlreadyExistException;
import com.example.springsecurityregistration.error.UserNotFoundException;
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
    protected ResponseEntity<Object> handleBindException(BindException ex,
                                                         HttpHeaders headers,
                                                         HttpStatus status,
                                                         WebRequest request) {
        BindingResult result = ex.getBindingResult();
        GenericResponse bodyOfResponse = new GenericResponse(result.getFieldErrors(), result.getGlobalErrors());
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<Object> handleUserNotFound(RuntimeException ex, WebRequest request) {
        logger.error("404 Status Code", ex);
        GenericResponse responseBody = new GenericResponse(
                messages.getMessage("message.userNotFound", null, request.getLocale()), "UserNotFound");

        return handleExceptionInternal(ex, responseBody, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ UserAlreadyExistException.class })
    public ResponseEntity<Object> handleUserAlreadyExist(RuntimeException ex, WebRequest request) {
        logger.error("409 Status Code", ex);
        GenericResponse bodyOfResponse = new GenericResponse(
                messages.getMessage("message.regError", null, request.getLocale()), "UserAlreadyExist");

        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler({MailAuthenticationException.class})
    public ResponseEntity<Object> handleMail(RuntimeException ex, WebRequest request) {
        logger.error("500 Status Code", ex);
        GenericResponse responseBody = new GenericResponse(
                messages.getMessage("message.email.config.error", null, request.getLocale()), "MailError");

        return handleExceptionInternal(ex, responseBody, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleInternal(RuntimeException ex, WebRequest request) {
        logger.error("500 Status Code", ex);
        GenericResponse responseBody = new GenericResponse(
                messages.getMessage("message.error", null, request.getLocale()), "InternalError");

        return handleExceptionInternal(ex, responseBody, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

}
