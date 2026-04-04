package com.example.barbershop.domain.exception;

public class SamePasswordException extends RuntimeException {
    public SamePasswordException() {
        super("La nueva contraseña no puede ser igual a la anterior.");
    }
}
