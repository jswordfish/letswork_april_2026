package com.letswork.crm.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(
            ResponseStatusException ex
    ) {

        Map<String, Object> body = new HashMap<>();
        body.put("status", ex.getStatus().value());
        body.put("message", ex.getReason());
        body.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(body, ex.getStatus());
    }
}
