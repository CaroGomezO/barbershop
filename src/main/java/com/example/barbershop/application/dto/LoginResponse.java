package com.example.barbershop.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String token;
    private String role;
    private String names;
    private boolean isPasswordTemporary;
}
