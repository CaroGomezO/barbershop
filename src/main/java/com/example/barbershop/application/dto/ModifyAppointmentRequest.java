package com.example.barbershop.application.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ModifyAppointmentRequest {
    @Schema(description = "Nuevo empleado. Dejar vacío para no modificar", example = "3")
    private Long employeeId;

    @Schema(description = "Nueva fecha. Dejar vacío para no modificar", example = "2026-04-10")
    private LocalDate date;

    @Schema(type = "string", format = "time", description = "Nueva hora de inicio. Dejar vacío para no modificar", example = "10:00:00")
    private LocalTime startTime;

    @Size(min = 1, message = "Debes seleccionar al menos un servicio")
    @Schema(description = "Nuevos servicios. Dejar vacío para no modificar", example = "[1, 3]")
    private List<Long> serviceIds;
}
