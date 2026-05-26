package com.example.barbershop.domain.exception;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException() {
        super("Cliente no encontrado");
    }
}
