package com.labassistant.exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;


@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    @ExceptionHandler(WebSocketException.class)
    public ResponseEntity<?> handleWebSocketException(WebSocketException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Notification service unavailable");
    }
    @ExceptionHandler(SseException.class)
    public ResponseEntity<?> handleSseExceptions(SseException ex) {
        logger.error("SSE Error: {}", ex.getMessage());
        return ResponseEntity.status(503).body(Map.of("error", "Notification service unavailable"));
    }
    @ExceptionHandler(AIValidationException.class)
    public ResponseEntity<Map<String, String>> handleAIValidationException(AIValidationException ex) {
        return ResponseEntity.badRequest()
            .body(Collections.singletonMap("aiValidation", ex.getMessage()));
    }

    @ExceptionHandler(InvalidAiValidationException.class)
    public ResponseEntity<Map<String, String>> handleInvalidAiValidationException(InvalidAiValidationException ex) {
        return ResponseEntity.badRequest()
            .body(Collections.singletonMap("aiValidation", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
            .body(Collections.singletonMap("aiValidation", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Collections.singletonMap("error", ex.getMessage()));
    }
}
