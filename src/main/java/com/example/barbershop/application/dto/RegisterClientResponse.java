package com.example.barbershop.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterClientResponse {
    private String token;
    private String names;
    private String message;
}
