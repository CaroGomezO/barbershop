package com.example.barbershop.application.port.in;

import java.util.List;

import com.example.barbershop.application.dto.RegisterEmployeeRequest;
import com.example.barbershop.application.dto.RegisterEmployeeResponse;

public interface RegisterEmployeeUseCase {
    RegisterEmployeeResponse register(RegisterEmployeeRequest request);
    List<RegisterEmployeeResponse> getAll();
}