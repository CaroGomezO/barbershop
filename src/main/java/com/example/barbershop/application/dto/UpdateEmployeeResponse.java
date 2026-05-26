package com.example.barbershop.application.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateEmployeeResponse {
    private Long employeeId;
    private String names;
    private String lastNames;
    private String phone;
    private String address;
    private boolean isActive;
    private List<String> services;
    private String message;
}
