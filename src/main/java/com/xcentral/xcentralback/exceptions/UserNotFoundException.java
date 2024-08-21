package com.xcentral.xcentralback.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("Could not find user " + id);
    }

    public UserNotFoundException(String username, boolean isUsername) {
        super("Could not find user " + username);
    }

    public UserNotFoundException(String email) {
        super("Could not find user " + email);
    }
}