package com.example.barbershop.infrastructure.adapter.out.persistence.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.barbershop.domain.model.Role;
import com.example.barbershop.infrastructure.adapter.out.persistence.entity.RoleEntity;

public interface RoleJpaRepository extends JpaRepository<RoleEntity, Integer> {
    Optional<RoleEntity> findByName(Role name);
}
