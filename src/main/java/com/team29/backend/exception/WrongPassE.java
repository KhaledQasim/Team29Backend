package com.team29.backend.exception;

public class WrongPassE extends RuntimeException {
    public WrongPassE() {
        super("Password must be longer than 8 characters!");
    }
}
