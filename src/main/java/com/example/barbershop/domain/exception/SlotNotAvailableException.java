package com.example.barbershop.domain.exception;

public class SlotNotAvailableException extends RuntimeException{
    public SlotNotAvailableException() {
        super("El horario seleccionado ya no está disponible, por favor elige otro.");
    }
}
