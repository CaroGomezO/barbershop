package com.example.barbershop.application.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmployeeAvailabilityResponse {
    private Long id;
    private String names;
    private String lastNames;
    private List<String> services;
}
