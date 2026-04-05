package com.example.barbershop.domain.model;

import java.math.BigDecimal;

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
public class AppointmentDetail {
    private Long id;
    private Appointment appointment;
    private Service service;
    private BigDecimal price;
    private Integer durationMinutes;
}
