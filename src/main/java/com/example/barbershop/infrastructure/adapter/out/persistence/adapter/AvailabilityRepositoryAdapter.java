package com.example.barbershop.infrastructure.adapter.out.persistence.adapter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.barbershop.application.port.out.AvailabilityRepositoryPort;
import com.example.barbershop.domain.model.Availability;
import com.example.barbershop.infrastructure.adapter.out.persistence.entity.AvailabilityEntity;
import com.example.barbershop.infrastructure.adapter.out.persistence.jpa.AvailabilityJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AvailabilityRepositoryAdapter implements AvailabilityRepositoryPort {
    private final AvailabilityJpaRepository jpaRepository;
    private final EmployeeRepositoryAdapter employeeAdapter;

    @Override
    public Availability save(Availability availability) {
        AvailabilityEntity entity = AvailabilityEntity.builder()
                .employee(employeeAdapter.toEntity(availability.getEmployee().getId()))
                .date(availability.getDate())
                .startTime(availability.getStartTime())
                .endTime(availability.getEndTime())
                .build();
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<Availability> findByEmployeeIdAndDate(Long employeeId, LocalDate date) {
        return jpaRepository.findByEmployeeIdAndDate(employeeId, date)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<LocalDate> findAvailableDatesByEmployee(
            Long employeeId, LocalDate from, LocalDate to) {
        return jpaRepository.findAvailableDatesByEmployee(employeeId, from, to);
    }

    @Override
    public List<LocalDate> findAvailableDatesByService(
            Long serviceId, LocalDate from, LocalDate to) {
        return jpaRepository.findAvailableDatesByService(serviceId, from, to);
    }

    @Override
    public boolean existsByEmployeeIdAndDateAndStartTime(
            Long employeeId, LocalDate date, LocalTime startTime) {
        return jpaRepository.existsByEmployeeIdAndDateAndStartTime(
                employeeId, date, startTime);
    }

    private Availability toDomain(AvailabilityEntity e) {
        return Availability.builder()
                .id(e.getId())
                .employee(employeeAdapter.toDomain(e.getEmployee()))
                .date(e.getDate())
                .startTime(e.getStartTime())
                .endTime(e.getEndTime())
                .build();
    }
}
