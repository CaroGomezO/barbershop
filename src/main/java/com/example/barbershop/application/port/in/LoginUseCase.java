package com.example.barbershop.application.port.in;

import com.example.barbershop.application.dto.LoginRequest;
import com.example.barbershop.application.dto.LoginResponse;

public interface LoginUseCase {
    LoginResponse login(LoginRequest request);
}
