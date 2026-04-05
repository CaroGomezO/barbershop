package com.example.barbershop.application.port.in;

import com.example.barbershop.application.dto.RegisterEmployeeRequest;
import com.example.barbershop.application.dto.RegisterEmployeeResponse;

public interface RegisterEmployeeUseCase {
    RegisterEmployeeResponse register(RegisterEmployeeRequest request);
}
