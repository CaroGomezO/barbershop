package com.example.barbershop.domain.exception;

public class ServiceNotFoundException extends RuntimeException {
    public ServiceNotFoundException(Long serviceId) {
        super("El servicio con ID " + serviceId + " no fue encontrado.");
    }

}
