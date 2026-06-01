package com.example.barbershop.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.barbershop.application.dto.UpdateClientRequest;
import com.example.barbershop.application.dto.UpdateClientResponse;
import com.example.barbershop.application.port.in.UpdateClientUseCase;
import com.example.barbershop.application.port.out.ClientRepositoryPort;
import com.example.barbershop.application.port.out.UserRepositoryPort;
import com.example.barbershop.domain.exception.ClientNotFoundException;
import com.example.barbershop.domain.exception.DuplicateResourceException;
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

        // Validar que el solicitante exista y esté autenticado
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(UnauthorizedAccessException::new);

        // Buscar el cliente a editar
        Client client = clientRepository.findById(clientId)
                .orElseThrow(ClientNotFoundException::new);

        // Verificar permisos: solo admin o el propio usuario
        boolean isAdmin = requester.getRole() == Role.ADMINISTRADOR;
        boolean isOwner = client.getUser().getEmail().equals(requesterEmail);

        if (!isAdmin && !isOwner) {
            throw new UnauthorizedAccessException();
        }

        // Validar y aplicar cambios solo si vienen en la petición
        if (request.getNames() != null && !request.getNames().isBlank()) {
            client.setNames(request.getNames());
        }

        if (request.getLastNames() != null && !request.getLastNames().isBlank()) {
            client.setLastNames(request.getLastNames());
        }

        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            // Verificar que el teléfono no esté en uso por otro cliente
            if (clientRepository.existsByPhoneNumberAndIdNot(request.getPhone(), clientId)) {
                throw new DuplicateResourceException(
                    "El número de teléfono ya está registrado por otro cliente");
            }
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