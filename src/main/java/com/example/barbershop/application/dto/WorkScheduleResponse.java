package com.example.barbershop.application.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkScheduleResponse {
    private Long id;
    private Long employeeId;
    private String employeeNames;
    private String employeeLastNames;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}
