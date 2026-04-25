package com.example.barbershop.infrastructure.adapter.in.web;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.barbershop.domain.exception.AppointmentAlreadyCancelledException;
import com.example.barbershop.domain.exception.AppointmentNotFoundException;
import com.example.barbershop.domain.exception.AppointmentNotOwnedByUserException;
import com.example.barbershop.domain.exception.BarberCancellationReasonNotProvidedException;
import com.example.barbershop.domain.exception.CancellationLimitExceededException;
import com.example.barbershop.domain.exception.DocumentAlreadyExistsException;
import com.example.barbershop.domain.exception.EmailAlreadyExistsException;
import com.example.barbershop.domain.exception.EmployeeNotFoundException;
import com.example.barbershop.domain.exception.InvalidCredentialsException;
import com.example.barbershop.domain.exception.InvalidScheduleException;
import com.example.barbershop.domain.exception.PasswordMismatchException;
import com.example.barbershop.domain.exception.SamePasswordException;
import com.example.barbershop.domain.exception.ServiceNotAvailableException;
import com.example.barbershop.domain.exception.ServiceNotFoundException;
import com.example.barbershop.domain.exception.SlotNotAvailableException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentials(
            InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler({EmailAlreadyExistsException.class,
                       DocumentAlreadyExistsException.class,
                       SlotNotAvailableException.class,
                       ServiceNotAvailableException.class,
                        AppointmentAlreadyCancelledException.class})
    public ResponseEntity<Map<String, String>> handleConflict(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler({PasswordMismatchException.class,
                       SamePasswordException.class,
                       InvalidScheduleException.class,
                        CancellationLimitExceededException.class,
                        AppointmentNotOwnedByUserException.class,
                        BarberCancellationReasonNotProvidedException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler({ServiceNotFoundException.class,
                        EmployeeNotFoundException.class,
                        AppointmentNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        f -> f.getField(),
                        f -> f.getDefaultMessage(),
                        (a, b) -> a
                ));
        return ResponseEntity.badRequest()
                .body(Map.of("errors", errors));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(
            IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", ex.getMessage()));
    }
}
