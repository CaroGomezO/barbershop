package com.example.barbershop.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendaItem {

    private Long appointmentId;
    private String clientFullName;
    private String serviceName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private AppointmentStatus status;

    public boolean isCancelled() {
        return AppointmentStatus.CANCELADA.equals(this.status);
    }
}
