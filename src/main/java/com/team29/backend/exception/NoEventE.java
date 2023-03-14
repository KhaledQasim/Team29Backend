package com.team29.backend.exception;

public class NoEventE extends RuntimeException {
    public NoEventE(Long id) {
        super("Could not find the calendar event matching this id: " + id);
    }
}
