package com.example.barbershop.domain.exception;

public class ServiceNotAssignedToEmployeeException extends RuntimeException {
    public ServiceNotAssignedToEmployeeException(String serviceName) {
        super("El servicio '" + serviceName + "' no está asignado al empleado seleccionado.");
    }
}
