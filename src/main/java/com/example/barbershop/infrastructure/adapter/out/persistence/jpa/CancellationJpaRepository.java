package com.example.barbershop.infrastructure.adapter.out.persistence.jpa;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.barbershop.infrastructure.adapter.out.persistence.entity.CancellationEntity;

public interface CancellationJpaRepository extends JpaRepository< CancellationEntity, Long > {

    Long countByUserIdAndCancellationDateBetween(Long userId, LocalDateTime from, LocalDateTime to);
}
