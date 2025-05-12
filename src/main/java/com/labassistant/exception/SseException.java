package com.labassistant.exception;

public class SseException extends RuntimeException {
    public SseException(String message) {
        super(message);
    }
    
    public SseException(String message, Throwable cause) {
        super(message, cause);
    }
}