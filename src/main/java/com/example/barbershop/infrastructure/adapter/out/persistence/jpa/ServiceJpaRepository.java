package com.example.barbershop.infrastructure.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.barbershop.infrastructure.adapter.out.persistence.entity.ServiceEntity;

public interface ServiceJpaRepository extends JpaRepository<ServiceEntity, Long> {

}
