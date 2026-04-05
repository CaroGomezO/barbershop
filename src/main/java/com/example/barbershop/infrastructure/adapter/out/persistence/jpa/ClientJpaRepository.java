package com.example.barbershop.infrastructure.adapter.out.persistence.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.barbershop.infrastructure.adapter.out.persistence.entity.ClientEntity;

public interface ClientJpaRepository extends JpaRepository<ClientEntity, Long> {
    Optional<ClientEntity> findByUserEmail(String email);
}
