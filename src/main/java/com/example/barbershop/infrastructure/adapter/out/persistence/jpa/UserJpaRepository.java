package com.example.barbershop.infrastructure.adapter.out.persistence.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.barbershop.infrastructure.adapter.out.persistence.entity.UserEntity;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
