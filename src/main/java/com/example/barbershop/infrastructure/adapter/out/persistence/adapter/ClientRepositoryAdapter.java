package com.example.barbershop.infrastructure.adapter.out.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.barbershop.application.port.out.ClientRepositoryPort;
import com.example.barbershop.domain.model.Client;
import com.example.barbershop.infrastructure.adapter.out.persistence.entity.ClientEntity;
import com.example.barbershop.infrastructure.adapter.out.persistence.jpa.ClientJpaRepository;
import com.example.barbershop.infrastructure.adapter.out.persistence.jpa.UserJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClientRepositoryAdapter implements ClientRepositoryPort {
    private final ClientJpaRepository jpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final UserRepositoryAdapter userAdapter;

    @Override
    public Client save(Client client) {
        ClientEntity entity = ClientEntity.builder()
                .user(userJpaRepository.findById(client.getUser().getId())
                        .orElseThrow(() -> new IllegalStateException("Usuario no encontrado")))
                .names(client.getNames())
                .lastNames(client.getLastNames())
                .phoneNumber(client.getPhoneNumber())
                .build();
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Client> findByUserEmail(String email) {
        return jpaRepository.findByUserEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<Client> findById(Long clientId) {
        return jpaRepository.findById(clientId).map(this::toDomain);
    }

    @Override
    public Client update(Client client) {
        ClientEntity entity = jpaRepository.findById(client.getId())
                .orElseThrow(() -> new IllegalStateException("Cliente no encontrado"));

        if (client.getNames() != null)       entity.setNames(client.getNames());
        if (client.getLastNames() != null)   entity.setLastNames(client.getLastNames());
        if (client.getPhoneNumber() != null) entity.setPhoneNumber(client.getPhoneNumber());

        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long clientId) {
        return jpaRepository.existsByPhoneNumberAndIdNot(phoneNumber, clientId);
    }

    @Override
    public List<Client> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public Client toDomain(ClientEntity e) {
        return Client.builder()
                .id(e.getId())
                .user(userAdapter.toDomain(e.getUser()))
                .names(e.getNames())
                .lastNames(e.getLastNames())
                .phoneNumber(e.getPhoneNumber())
                .build();
    }
}