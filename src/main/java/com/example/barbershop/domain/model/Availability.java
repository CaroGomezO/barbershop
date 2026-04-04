package com.example.barbershop.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Availability {
    private Long id;
    private Employee employee;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}
