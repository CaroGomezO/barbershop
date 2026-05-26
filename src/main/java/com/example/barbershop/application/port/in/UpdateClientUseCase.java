package com.example.barbershop.application.port.in;

import com.example.barbershop.application.dto.UpdateClientRequest;
import com.example.barbershop.application.dto.UpdateClientResponse;

public interface UpdateClientUseCase {
    UpdateClientResponse update(Long clientId, String requesterEmail, UpdateClientRequest request);
}
