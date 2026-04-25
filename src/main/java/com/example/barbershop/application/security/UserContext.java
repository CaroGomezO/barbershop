package com.example.barbershop.application.security;

import com.example.barbershop.domain.model.Role;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserContext {
    private Long userId;
    private Role role;
}
