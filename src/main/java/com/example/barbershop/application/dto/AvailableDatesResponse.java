package com.example.barbershop.application.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AvailableDatesResponse {
    private List<LocalDate> availableDates;
    private List<LocalDate> disabledDates;
}
