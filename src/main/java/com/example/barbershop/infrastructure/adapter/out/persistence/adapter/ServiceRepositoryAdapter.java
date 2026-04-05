package com.example.barbershop.infrastructure.adapter.out.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.barbershop.application.port.out.ServiceRepositoryPort;
import com.example.barbershop.domain.model.Service;
import com.example.barbershop.infrastructure.adapter.out.persistence.entity.ServiceEntity;
import com.example.barbershop.infrastructure.adapter.out.persistence.jpa.ServiceJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ServiceRepositoryAdapter implements ServiceRepositoryPort {
    private final ServiceJpaRepository jpaRepository;

    @Override
    public List<Service> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Service> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Service> findAllById(List<Long> ids) {
        return jpaRepository.findAllById(ids).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    public Service toDomain(ServiceEntity e) {
        return Service.builder()
                .id(e.getId())
                .name(e.getName())
                .description(e.getDescription())
                .price(e.getPrice())
                .durationMinutes(e.getDurationMinutes())
                .build();
    }

    public ServiceEntity toEntity(Long id) {
        return jpaRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(
                        "Servicio no encontrado: " + id));
    }
}
