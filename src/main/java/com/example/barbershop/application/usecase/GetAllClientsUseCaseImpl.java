package com.example.barbershop.application.usecase;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.barbershop.application.dto.UpdateClientResponse;
import com.example.barbershop.application.port.in.GetAllClientsUseCase;
import com.example.barbershop.application.port.out.ClientRepositoryPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetAllClientsUseCaseImpl implements GetAllClientsUseCase {

    private final ClientRepositoryPort clientRepository;

    @Override
    public List<UpdateClientResponse> getAll() {
        return clientRepository.findAll()
                .stream()
                .map(client -> UpdateClientResponse.builder()
                        .clientId(client.getId())
                        .names(client.getNames())
                        .lastNames(client.getLastNames())
                        .phone(client.getPhoneNumber())
                        .email(client.getUser().getEmail())
                        .build())
                .collect(Collectors.toList());
    }
}