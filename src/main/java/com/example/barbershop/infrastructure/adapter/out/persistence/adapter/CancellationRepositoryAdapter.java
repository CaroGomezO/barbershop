package com.example.barbershop.infrastructure.adapter.out.persistence.adapter;

import java.time.LocalDateTime;

import org.springframework.stereotype.Repository;

import com.example.barbershop.application.port.out.CancellationRepositoryPort;
import com.example.barbershop.domain.model.Cancellation;
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
            .user(cancellation.getUser())
            .appointment(cancellation.getAppointment())
            .cancellationDate(cancellation.getCancellationDate())
            .reason(cancellation.getReason())
            .role(cancellation.getRole())
            .build();
            
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Long countByUserIdAndCancellationDateBetween(Long userId, LocalDateTime from, LocalDateTime to){
        return jpaRepository.countByUserIdAndCancellationDateBetween(userId, from, to);
    }

    public Cancellation toDomain(CancellationEntity c){
        return Cancellation.builder()
                .id(c.getId())
                .user(c.getUser())
                .appointment(c.getAppointment())
                .cancellationDate(c.getCancellationDate())
                .reason(c.getReason())
                .role(c.getRole())
                .build();
    }

}
