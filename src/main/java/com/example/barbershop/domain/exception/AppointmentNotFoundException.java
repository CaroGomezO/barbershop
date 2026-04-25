package com.example.barbershop.domain.exception;

public class AppointmentNotFoundException extends RuntimeException {
    public AppointmentNotFoundException(Long appointmentId){
        super("La cita con ID " + appointmentId + " no fue encontrada.");
    }
}
