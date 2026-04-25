package com.example.barbershop.domain.exception;

public class AppointmentAlreadyCancelledException extends RuntimeException {
    public AppointmentAlreadyCancelledException(Long appointmentId){
        super("La cita con ID " + appointmentId + " ya ha sido cancelada previamente.");
    }
}
