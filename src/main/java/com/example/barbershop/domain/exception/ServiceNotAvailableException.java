package com.example.barbershop.domain.exception;

public class ServiceNotAvailableException extends RuntimeException {
    public ServiceNotAvailableException() {
        super("El servicio no tiene disponibilidad en este momento.");
    }

}
