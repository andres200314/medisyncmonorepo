package com.medisync.medisync.domain.exceptions;

public class InvalidPasswordException extends BusinessRuleViolationException {
    
    public InvalidPasswordException(String message) {
        super(message);
    }
    
    public InvalidPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}