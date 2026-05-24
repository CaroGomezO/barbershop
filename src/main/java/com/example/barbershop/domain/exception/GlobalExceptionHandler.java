package com.example.barbershop.domain.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler extends RuntimeException{
    @ExceptionHandler(WorkScheduleNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleWorkScheduleNotFound(
            WorkScheduleNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(OverlappingScheduleException.class)
    public ResponseEntity<Map<String, String>> handleOverlapping(
            OverlappingScheduleException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }
}
