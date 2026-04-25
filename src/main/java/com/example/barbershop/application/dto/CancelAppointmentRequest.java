package com.example.barbershop.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;


@Getter
public class CancelAppointmentRequest {
    @NotNull(message = "Es obligatorio el id de la cita")
    private Long appointmentId;

    private String reason;
}
