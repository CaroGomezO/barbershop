package com.example.barbershop.application.port.out;

import java.util.List;
import java.util.Optional;

import com.example.barbershop.domain.model.Service;

public interface ServiceRepositoryPort {
    List<Service> findAll();
    List<Service> findAllActive();
    Optional<Service> findById(Long id);
    List<Service> findAllById(List<Long> ids);

    Service save(Service service);
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByName(String name);
    Optional<Service> findByName(String name);
    Service update(Service service);
    long countActiveAppointmentsByServiceId(Long serviceId);
}
