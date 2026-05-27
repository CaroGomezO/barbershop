package com.example.barbershop.domain.exception;

public class ServiceAlreadyDisabledException extends RuntimeException {
    public ServiceAlreadyDisabledException() {
        super("El servicio ya se encuentra deshabilitado");
    }
}
