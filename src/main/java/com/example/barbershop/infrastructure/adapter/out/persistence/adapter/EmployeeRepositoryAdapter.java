package com.example.barbershop.infrastructure.adapter.out.persistence.adapter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.barbershop.application.port.out.EmployeeRepositoryPort;
import com.example.barbershop.domain.model.Employee;
import com.example.barbershop.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import com.example.barbershop.infrastructure.adapter.out.persistence.jpa.EmployeeJpaRepository;
import com.example.barbershop.infrastructure.adapter.out.persistence.jpa.UserJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmployeeRepositoryAdapter implements EmployeeRepositoryPort {
    private final EmployeeJpaRepository jpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final UserRepositoryAdapter userAdapter;
    private final ServiceRepositoryAdapter serviceAdapter;

    @Override
    public Employee save(Employee employee) {
        EmployeeEntity entity = EmployeeEntity.builder()
                .user(userJpaRepository.findById(employee.getUser().getId())
                        .orElseThrow(() -> new IllegalStateException(
                                "Usuario no encontrado")))
                .documentNumber(employee.getDocumentNumber())
                .names(employee.getNames())
                .lastNames(employee.getLastNames())
                .phoneNumber(employee.getPhoneNumber())
                .address(employee.getAddress())
                .isActive(employee.isActive())
                .services(employee.getServices().stream()
                        .map(s -> serviceAdapter.toEntity(s.getId()))
                        .collect(Collectors.toSet()))
                .build();
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Employee> findByUserEmail(String email) {
        return jpaRepository.findByUserEmail(email).map(this::toDomain);
    }

    @Override
    public boolean existsByDocumentNumber(String documentNumber) {
        return jpaRepository.existsByDocumentNumber(documentNumber);
    }

    @Override
    public List<Employee> findActiveEmployeesByServiceWithAvailability(
            Long serviceId, LocalDate from, LocalDate to) {
        return jpaRepository.findActiveByServiceWithAvailability(serviceId, from, to)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    public Employee toDomain(EmployeeEntity e) {
        return Employee.builder()
                .id(e.getId())
                .user(userAdapter.toDomain(e.getUser()))
                .documentNumber(e.getDocumentNumber())
                .names(e.getNames())
                .lastNames(e.getLastNames())
                .phoneNumber(e.getPhoneNumber())
                .address(e.getAddress())
                .isActive(e.isActive())
                .services(e.getServices().stream()
                        .map(serviceAdapter::toDomain)
                        .collect(Collectors.toSet()))
                .build();
    }

    public EmployeeEntity toEntity(Long id) {
        return jpaRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(
                        "Empleado no encontrado: " + id));
    }
}
