package com.team29.backend.exception;

public class UserNotFoundE extends RuntimeException {
    public UserNotFoundE(Long id) {
        super("Could not find the calendar event matching this id: " + id);
    }
}
