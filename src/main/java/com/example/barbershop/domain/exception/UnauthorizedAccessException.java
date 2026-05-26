package com.example.barbershop.domain.exception;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException() {
        super("No tienes permisos para realizar esta acción");
    }
}
