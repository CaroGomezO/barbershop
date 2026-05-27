package com.example.barbershop.infrastructure.adapter.out.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.barbershop.infrastructure.adapter.out.persistence.entity.ServiceEntity;

@Repository
public interface ServiceJpaRepository extends JpaRepository<ServiceEntity, Long> {

    Optional<ServiceEntity> findByNameIgnoreCase(String name);
    
    boolean existsByNameIgnoreCase(String name);

    List<ServiceEntity> findByIsActiveTrue();

    @Query("""
        SELECT COUNT(DISTINCT ade.appointment.id)
        FROM AppointmentDetailEntity ade
        WHERE ade.service.id = :serviceId
          AND ade.appointment.status = 'CONFIRMADA'
        """)
    long countActiveAppointmentsByServiceId(@Param("serviceId") Long serviceId);
}