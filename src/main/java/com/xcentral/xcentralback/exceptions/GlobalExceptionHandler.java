package com.xcentral.xcentralback.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserNotFound(UserNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return response;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidCredentialsException.class)
    public Map<String, String> handleInvalidCredentials(InvalidCredentialsException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return response;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public Map<String, Object> handleBadCredentials(org.springframework.security.authentication.BadCredentialsException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Invalid username or password");
        response.put("code", "INVALID_CREDENTIALS");
        return response;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(org.springframework.security.authentication.DisabledException.class)
    public Map<String, Object> handleDisabledException(org.springframework.security.authentication.DisabledException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Account is disabled");
        response.put("code", "ACCOUNT_DISABLED");
        return response;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public Map<String, Object> handleAccessDenied(org.springframework.security.access.AccessDeniedException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Access denied");
        response.put("code", "ACCESS_DENIED");
        return response;
    }
}