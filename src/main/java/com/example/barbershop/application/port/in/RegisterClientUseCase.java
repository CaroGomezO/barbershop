package com.example.barbershop.application.port.in;

import com.example.barbershop.application.dto.RegisterClientRequest;
import com.example.barbershop.application.dto.RegisterClientResponse;

public interface RegisterClientUseCase {
    RegisterClientResponse register(RegisterClientRequest request);
}
