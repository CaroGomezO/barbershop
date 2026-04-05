package com.example.barbershop.application.dto;

import java.time.LocalTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SlotResponse {
    private LocalTime startTime;
    private LocalTime endTime;
}
