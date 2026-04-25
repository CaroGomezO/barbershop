package com.example.barbershop.domain.exception;

public class BarberCancellationReasonNotProvidedException extends RuntimeException {
     public BarberCancellationReasonNotProvidedException() {
        super("El barbero debe proveer un motivo de cancelacion para la cita ");
    }
}
