package com.example.barbershop.domain.exception;

public class DocumentAlreadyExistsException extends RuntimeException {
    public DocumentAlreadyExistsException(String documento){
        super("El documento " + documento + " ya está registrado.");
    }
}
