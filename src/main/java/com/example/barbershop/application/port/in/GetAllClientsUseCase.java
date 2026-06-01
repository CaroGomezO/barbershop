package com.example.barbershop.application.port.in;

import java.util.List;

import com.example.barbershop.application.dto.UpdateClientResponse;

public interface GetAllClientsUseCase {
    List<UpdateClientResponse> getAll();
}