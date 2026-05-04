package com.example.barbershop.infrastructure.adapter.out.persistence.adapter;

import java.time.LocalDateTime;

import org.springframework.stereotype.Repository;

import com.example.barbershop.application.port.out.CancellationRepositoryPort;
import com.example.barbershop.domain.model.Appointment;
import com.example.barbershop.domain.model.AppointmentStatus;
import com.example.barbershop.domain.model.Cancellation;
import com.example.barbershop.domain.model.Role;
import com.example.barbershop.infrastructure.adapter.out.persistence.entity.CancellationEntity;
import com.example.barbershop.infrastructure.adapter.out.persistence.jpa.AppointmentJpaRepository;
import com.example.barbershop.infrastructure.adapter.out.persistence.jpa.CancellationJpaRepository;
import com.example.barbershop.infrastructure.adapter.out.persistence.jpa.UserJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CancellationRepositoryAdapter implements CancellationRepositoryPort {

    private final CancellationJpaRepository jpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final AppointmentJpaRepository appointmentJpaRepository;

    
    @Override
    public Cancellation save(Cancellation cancellation) {
        CancellationEntity entity = CancellationEntity.builder()
            .user(userJpaRepository.findById(cancellation.getUser().getId())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado")))
            .appointment(appointmentJpaRepository.findById(cancellation.getAppointment().getId())
                .orElseThrow(() -> new IllegalStateException("Cita no encontrada")))
            .cancellationDate(cancellation.getCancellationDate())
            .reason(cancellation.getReason())
            .cancelledBy(cancellation.getRole() != null ? (long) cancellation.getRole().ordinal() + 1 : 1L)
            .build();
            
        CancellationEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Long countByUserIdAndCancellationDateBetween(Long userId, LocalDateTime from, LocalDateTime to) {
        return jpaRepository.countByUserIdAndCancellationDateBetween(userId, from, to);
    }

    private Cancellation toDomain(CancellationEntity entity) {
    if (entity == null) return null;
    
    return Cancellation.builder()
        .id(entity.getId())
        .user(null)
        .appointment(Appointment.builder()
            .id(entity.getAppointment().getId())
            .status(entity.getAppointment().getStatus())
            .build())
        .cancellationDate(entity.getCancellationDate())
        .reason(entity.getReason())
        .role(getRoleFromId(entity.getCancelledBy()))
        .build();
    }
    
    private Role getRoleFromId(Long roleId) {
        if (roleId == null) return Role.CLIENTE;
        if (roleId == 1) return Role.ADMINISTRADOR;
        if (roleId == 2) return Role.BARBERO;
        return Role.CLIENTE;
    }
}