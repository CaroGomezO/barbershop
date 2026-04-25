package com.example.barbershop.domain.exception;

public class CancellationLimitExceededException extends RuntimeException {
    public CancellationLimitExceededException() {
        super("Has sobrepasado el límite de cancelaciones por mes");
    }
}