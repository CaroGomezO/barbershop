package com.example.barbershop.domain.exception;

public class OverlappingScheduleException extends RuntimeException {
    public OverlappingScheduleException() {
        super("Ya existe una jornada configurada para ese día");
    } 
}
