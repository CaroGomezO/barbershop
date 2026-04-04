package com.example.barbershop.domain.exception;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException() {
        super("Las contraseñas no coinciden.");
    }
}
