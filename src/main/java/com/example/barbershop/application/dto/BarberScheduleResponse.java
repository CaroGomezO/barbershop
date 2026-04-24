package com.example.barbershop.application.dto;

import lombok.Getter;

import java.util.List;

import lombok.Builder;

@Getter
@Builder
public class BarberScheduleResponse {
    private Long employeeId;
    private String names;
    private String lastNames;
    private List<DayScheduleDto> days;

    @Getter
    @Builder
    public static class DayScheduleDto {
        private String date;
        private List<SlotDto> slots;
    }

    @Getter
    @Builder
    public static class SlotDto {
        private String startTime;
        private String endTime;
        private String status;
        private AppointmentInfoDto appointment;
    }

    @Getter
    @Builder
    public static class AppointmentInfoDto {
        private Long appointmentId;
        private String clientNames;
        private String clientLastNames;
        private List<String> services;
        private String totalPrice;
    }
}
