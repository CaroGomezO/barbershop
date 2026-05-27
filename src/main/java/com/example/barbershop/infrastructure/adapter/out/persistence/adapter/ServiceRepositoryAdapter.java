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
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Service> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Service> findAllById(List<Long> ids) {
        return jpaRepository.findAllById(ids).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Service save(Service service) {
        ServiceEntity entity = toEntity(service);
        ServiceEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public Optional<Service> findByName(String name) {
        return jpaRepository.findByNameIgnoreCase(name).map(this::toDomain);
    }

    public Service toDomain(ServiceEntity entity) {
        if (entity == null) return null;
        
        return Service.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .durationMinutes(entity.getDurationMinutes())
                .isActive(entity.isActive())
                .build();
    }

    public ServiceEntity toEntity(Service service) {
        if (service == null) return null;
        
        return ServiceEntity.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .price(service.getPrice())
                .durationMinutes(service.getDurationMinutes())
                .build();
    }

    public ServiceEntity toEntity(Long id) {
        return jpaRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Servicio no encontrado: " + id));
    }

    @Override
    public List<Service> findAllActive() {
        return jpaRepository.findByIsActiveTrue().stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Service update(Service service) {
        ServiceEntity entity = jpaRepository.findById(service.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Servicio no encontrado"));
        entity.setActive(service.isActive());
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public long countActiveAppointmentsByServiceId(Long serviceId) {
        return jpaRepository.countActiveAppointmentsByServiceId(serviceId);
    }
}