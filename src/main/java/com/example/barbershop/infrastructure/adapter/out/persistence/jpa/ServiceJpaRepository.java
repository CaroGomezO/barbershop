package com.example.barbershop.infrastructure.adapter.out.persistence.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.barbershop.infrastructure.adapter.out.persistence.entity.ServiceEntity;

@Repository
public interface ServiceJpaRepository extends JpaRepository<ServiceEntity, Long> {

    Optional<ServiceEntity> findByNameIgnoreCase(String name);
    
    boolean existsByNameIgnoreCase(String name);
}