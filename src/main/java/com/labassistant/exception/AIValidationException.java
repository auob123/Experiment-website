package com.labassistant.exception;

public class AIValidationException extends RuntimeException {
    public AIValidationException(String message) {
        super("AI validation failed: " + message);
    }
}