package com.example.barbershop.application.port.out;

import java.util.List;
import java.util.Optional;

import com.example.barbershop.domain.model.Client;

public interface ClientRepositoryPort {
    Client save(Client client);
    Optional<Client> findByUserEmail(String email);
    Optional<Client> findById(Long clientId);
    Client update(Client client);
    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long clientId);
    List<Client> findAll();
}