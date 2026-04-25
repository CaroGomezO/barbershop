package com.example.barbershop.domain.exception;

public class AppointmentNotOwnedByUserException extends RuntimeException {
    public AppointmentNotOwnedByUserException(Long appointmentId, Long userId) {
        super(String.format("Appointment %d does not belong to user %d", appointmentId, userId));
    }
}
