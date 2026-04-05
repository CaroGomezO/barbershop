package com.example.barbershop.application.port.out;

import java.util.List;
import java.util.Optional;

import com.example.barbershop.domain.model.Service;

public interface ServiceRepositoryPort {
    List<Service> findAll();
    Optional<Service> findById(Long id);
    List<Service> findAllById(List<Long> ids);
}
