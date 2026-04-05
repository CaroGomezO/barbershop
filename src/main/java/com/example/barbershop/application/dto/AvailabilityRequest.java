package com.example.barbershop.application.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class AvailabilityRequest {
    @NotNull @Size(min = 1, message = "Debes configurar al menos un día")
    @Valid
    private List<DaySchedule> days;

    @Getter
    public static class DaySchedule {
        @NotNull(message = "La fecha es obligatoria")
        @Future(message = "Solo puedes configurar fechas futuras")
        private LocalDate date;

        @NotNull(message = "La hora de inicio es obligatoria")
        private LocalTime startTime;

        @NotNull(message = "La hora de fin es obligatoria")
        private LocalTime endTime;
    }
}
