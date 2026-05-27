package com.example.barbershop.application.port.in;

import com.example.barbershop.application.dto.DisableServiceResponse;

public interface DisableServiceUseCase {
    DisableServiceResponse disable(Long serviceId);
}
