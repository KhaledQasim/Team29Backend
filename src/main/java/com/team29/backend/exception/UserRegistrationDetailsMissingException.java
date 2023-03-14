package com.team29.backend.exception;

public class UserRegistrationDetailsMissingException extends RuntimeException{
    public UserRegistrationDetailsMissingException() {
        super ("Missing Registration Details!");
    }
    
 
}
