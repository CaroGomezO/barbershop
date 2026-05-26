package com.example.barbershop.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateClientResponse {
    private Long clientId;
    private String names;
    private String lastNames;
    private String phone;
    private String email;
    private String message;
}
