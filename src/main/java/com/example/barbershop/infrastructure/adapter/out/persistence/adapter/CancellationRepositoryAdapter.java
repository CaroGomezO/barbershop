package com.example.barbershop.infrastructure.adapter.out.persistence.adapter;

import java.time.LocalDateTime;

import org.springframework.stereotype.Repository;

import com.example.barbershop.application.port.out.CancellationRepositoryPort;
import com.example.barbershop.domain.model.Cancellation;
import com.example.barbershop.domain.model.Role;
import com.example.barbershop.infrastructure.adapter.out.persistence.entity.CancellationEntity;
import com.example.barbershop.infrastructure.adapter.out.persistence.jpa.CancellationJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CancellationRepositoryAdapter implements CancellationRepositoryPort {

    private final CancellationJpaRepository jpaRepository;

    @Override
    public Cancellation save(Cancellation cancellation) {
        CancellationEntity entity = CancellationEntity.builder()
            .user(null)  // TODO: Necesitas convertir User a UserEntity
            .appointment(null)  // TODO: Necesitas convertir Appointment a AppointmentEntity
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
            .user(null)  // TODO: Necesitas convertir UserEntity a User
            .appointment(null)  // TODO: Necesitas convertir AppointmentEntity a Appointment
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