package com.example.barbershop.application.dto;


import java.time.LocalDateTime;

import com.example.barbershop.domain.model.AppointmentStatus;
import com.example.barbershop.domain.model.Role;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CancelAppointmentResponse {
    private Long id;
    private LocalDateTime cancellationDate;
    private AppointmentStatus status;
    private String reason;
    private Role cancelledBy;
}
