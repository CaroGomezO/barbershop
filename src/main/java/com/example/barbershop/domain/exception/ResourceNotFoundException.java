package com.example.barbershop.domain.exception;

public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s no encontrado con id: %d", resourceName, id));
    }
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
}