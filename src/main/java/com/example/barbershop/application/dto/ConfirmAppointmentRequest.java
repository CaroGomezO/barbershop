package com.example.barbershop.application.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ConfirmAppointmentRequest {
    @NotNull(message = "El empleado es obligatorio")
    private Long employeeId;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate date;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime startTime;

    @NotNull @Size(min = 1, message = "Selecciona al menos un servicio")
    private List<Long> serviceIds;
}
