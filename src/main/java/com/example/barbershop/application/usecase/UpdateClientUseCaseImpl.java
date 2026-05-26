package com.example.barbershop.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.barbershop.application.dto.UpdateClientRequest;
import com.example.barbershop.application.dto.UpdateClientResponse;
import com.example.barbershop.application.port.in.UpdateClientUseCase;
import com.example.barbershop.application.port.out.ClientRepositoryPort;
import com.example.barbershop.application.port.out.UserRepositoryPort;
import com.example.barbershop.domain.exception.ClientNotFoundException;
import com.example.barbershop.domain.exception.UnauthorizedAccessException;
import com.example.barbershop.domain.model.Client;
import com.example.barbershop.domain.model.Role;
import com.example.barbershop.domain.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateClientUseCaseImpl implements UpdateClientUseCase {
    private final ClientRepositoryPort clientRepository;
    private final UserRepositoryPort userRepository;

    @Override
    @Transactional
    public UpdateClientResponse update(Long clientId, String requesterEmail, UpdateClientRequest request) {

        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(UnauthorizedAccessException::new);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(ClientNotFoundException::new);

        boolean isAdmin = requester.getRole() == Role.ADMINISTRADOR;
        boolean isOwner = client.getUser().getEmail().equals(requesterEmail);

        if (!isAdmin && !isOwner) {
            throw new UnauthorizedAccessException();
        }

        if (request.getNames() != null && !request.getNames().isBlank()) {
            client.setNames(request.getNames());
        }
        if (request.getLastNames() != null && !request.getLastNames().isBlank()) {
            client.setLastNames(request.getLastNames());
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            client.setPhoneNumber(request.getPhone());
        }

        Client updated = clientRepository.update(client);

        return UpdateClientResponse.builder()
                .clientId(updated.getId())
                .names(updated.getNames())
                .lastNames(updated.getLastNames())
                .phone(updated.getPhoneNumber())
                .email(updated.getUser().getEmail())
                .message("Los datos del cliente han sido actualizados correctamente.")
                .build();
    }
}
