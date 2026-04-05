package com.example.barbershop.application.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterEmployeeResponse {
    private Long employeeId;
    private String names;
    private String lastNames;
    private String email;
    private String documentNumber;
    private String address;
    private boolean isActive;
    private List<String> services;
}
