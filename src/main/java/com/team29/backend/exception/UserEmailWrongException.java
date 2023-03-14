package com.team29.backend.exception;

public class UserEmailWrongException extends RuntimeException {
    public UserEmailWrongException() {
        super("Email is invalid");
    }
}
