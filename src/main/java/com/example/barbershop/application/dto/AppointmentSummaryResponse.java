package com.example.barbershop.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppointmentSummaryResponse {
    private String employeeNames;
    private String employeeLastNames;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<String> serviceNames;
    private BigDecimal totalPrice;
    private Integer totalDuration;
}
