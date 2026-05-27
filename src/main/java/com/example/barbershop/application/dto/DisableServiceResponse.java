package com.example.barbershop.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DisableServiceResponse {
    private Long serviceId;
    private String name;
    private boolean isActive;
    private long activeAppointmentsCount;
    private String message;
}
