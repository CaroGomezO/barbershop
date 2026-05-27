package com.example.barbershop.domain.exception;

public class AppointmentNotModifiableException extends RuntimeException {
    public AppointmentNotModifiableException() {
        super("La cita no puede ser modificada porque ya fue finalizada o cancelada");
    }
}
