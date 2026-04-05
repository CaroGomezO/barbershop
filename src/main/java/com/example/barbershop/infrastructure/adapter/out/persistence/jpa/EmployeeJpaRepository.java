package com.example.barbershop.infrastructure.adapter.out.persistence.jpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.barbershop.infrastructure.adapter.out.persistence.entity.EmployeeEntity;

public interface EmployeeJpaRepository extends JpaRepository<EmployeeEntity, Long> {
    Optional<EmployeeEntity> findByUserEmail(String email);

    boolean existsByDocumentNumber(String documentNumber);

    @Query("""
        SELECT DISTINCT e FROM EmployeeEntity e
        JOIN e.services s
        WHERE s.id = :serviceId
          AND e.isActive = true
          AND EXISTS (
              SELECT a FROM AvailabilityEntity a
              WHERE a.employee.id = e.id
                AND a.date > :from
                AND a.date <= :to
          )
    """)
    List<EmployeeEntity> findActiveByServiceWithAvailability(
            @Param("serviceId") Long serviceId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );
}
