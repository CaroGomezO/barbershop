package com.example.barbershop.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceResponse {
    private Long id;
    private String name;
    private String description;
    private String price;
    private Integer durationMinutes;
    private boolean isActive;
}