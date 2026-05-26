package com.example.barbershop.application.port.in;

import com.example.barbershop.application.dto.UpdateEmployeeRequest;
import com.example.barbershop.application.dto.UpdateEmployeeResponse;

public interface UpdateEmployeeUseCase {
    UpdateEmployeeResponse update(Long employeeId, UpdateEmployeeRequest request);
}
