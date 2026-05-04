package com.example.barbershop.infrastructure.adapter.out.persistence.adapter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.barbershop.application.port.out.AppointmentRepositoryPort;
import com.example.barbershop.domain.model.Appointment;
import com.example.barbershop.domain.model.AppointmentDetail;
import com.example.barbershop.domain.model.AppointmentStatus;
import com.example.barbershop.infrastructure.adapter.out.persistence.entity.AppointmentDetailEntity;
import com.example.barbershop.infrastructure.adapter.out.persistence.entity.AppointmentEntity;
import com.example.barbershop.infrastructure.adapter.out.persistence.entity.ClientEntity;
import com.example.barbershop.infrastructure.adapter.out.persistence.jpa.AppointmentJpaRepository;
import com.example.barbershop.infrastructure.adapter.out.persistence.jpa.ClientJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AppointmentRepositoryAdapter implements AppointmentRepositoryPort {
    private final AppointmentJpaRepository jpaRepository;
    private final ClientJpaRepository clientJpaRepository;
    private final EmployeeRepositoryAdapter employeeAdapter;
    private final ClientRepositoryAdapter clientAdapter;
    private final ServiceRepositoryAdapter serviceAdapter;

    @Override
    public Appointment save(Appointment appointment) {
        if (appointment.getId() != null) {
            jpaRepository.updateStatus(appointment.getId(), appointment.getStatus());
            return appointment;
        }

        ClientEntity clientEntity = clientJpaRepository
                .findById(appointment.getClient().getId())
                .orElseThrow(() -> new IllegalStateException("Cliente no encontrado"));

        AppointmentEntity entity = AppointmentEntity.builder()
            .client(clientEntity)
            .employee(employeeAdapter.toEntity(appointment.getEmployee().getId()))
            .date(appointment.getDate())
            .startTime(appointment.getStartTime())
            .endTime(appointment.getEndTime())
            .status(appointment.getStatus())
            .totalPrice(appointment.getTotalPrice())
            .build();

        AppointmentEntity saved = jpaRepository.save(entity);

        Set<AppointmentDetailEntity> detailEntities = appointment.getDetails().stream()
            .map(d -> AppointmentDetailEntity.builder()
                    .appointment(saved)
                    .service(serviceAdapter.toEntity(d.getService().getId()))
                    .price(d.getPrice())
                    .durationMinutes(d.getDurationMinutes())
                    .build())
            .collect(Collectors.toSet());

        saved.setDetails(detailEntities);
        return toDomain(jpaRepository.save(saved));
    }
    @Override
    public boolean existsConfirmedOverlap(Long employeeId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return jpaRepository.existsConfirmedOverlap(employeeId, date, startTime, endTime);
    }

    @Override
    public List<Appointment> findByClientEmail(String email) {
        return jpaRepository.findByClientUserEmail(email)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    private Appointment toDomain(AppointmentEntity e) {
        return Appointment.builder()
                .id(e.getId())
                .client(clientAdapter.toDomain(e.getClient()))
                .employee(employeeAdapter.toDomain(e.getEmployee()))
                .date(e.getDate())
                .startTime(e.getStartTime())
                .endTime(e.getEndTime())
                .status(e.getStatus())
                .totalPrice(e.getTotalPrice())
                .details(e.getDetails().stream()
                        .map(d -> AppointmentDetail.builder()
                                .id(d.getId())
                                .service(serviceAdapter.toDomain(d.getService()))
                                .price(d.getPrice())
                                .durationMinutes(d.getDurationMinutes())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }

    @Override
    public List<Appointment> findByEmployeeIdAndDateBetween(Long employeeId, LocalDate from, LocalDate to) {
        return jpaRepository.findConfirmedByEmployeeAndDateRange(employeeId, from, to)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Appointment> findById(Long appointmentId) {
        Optional<AppointmentEntity> appointmentEntity = jpaRepository
            .findById(appointmentId);
        return appointmentEntity.map(this::toDomain);
    }

    @Override
    public List<Appointment> findByDate(LocalDate date) {
        return jpaRepository.findByDate(date)
                .stream().map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Appointment> findByStatus(AppointmentStatus status) {
        return jpaRepository.findByStatus(status.name())
                .stream().map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Appointment> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        return jpaRepository.findByDateBetween(startDate, endDate)
                .stream().map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Appointment> findByEmployeeId(Long employeeId) {
        return jpaRepository.findByEmployeeId(employeeId)
                .stream().map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}