package com.example.barbershop.domain.exception;

public class WorkScheduleNotFoundException extends RuntimeException {
    public WorkScheduleNotFoundException() {
        super("Jornada laboral no encontrada");
    }
}
