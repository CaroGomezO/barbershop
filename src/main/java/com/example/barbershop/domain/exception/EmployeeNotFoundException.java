package com.example.barbershop.domain.exception;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException() {
        super("El empleado no fue encontrado.");
    }
}
