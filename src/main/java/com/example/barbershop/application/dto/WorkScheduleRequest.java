package com.example.barbershop.application.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class WorkScheduleRequest {
    @NotNull(message = "El día de la semana es obligatorio")
    @Schema(example = "MONDAY",
            allowableValues = {
                "MONDAY","TUESDAY","WEDNESDAY",
                "THURSDAY","FRIDAY","SATURDAY","SUNDAY"
            })
    private DayOfWeek dayOfWeek;

    @NotNull(message = "La hora de inicio es obligatoria")
    @Schema(type = "string", format = "time", example = "08:00:00")
    private LocalTime startTime;

    @NotNull(message = "La hora de fin es obligatoria")
    @Schema(type = "string", format = "time", example = "17:00:00")
    private LocalTime endTime;
}
