package com.example.barbershop.infrastructure.adapter.out.persistence.jpa;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.barbershop.infrastructure.adapter.out.persistence.entity.AvailabilityEntity;

public interface AvailabilityJpaRepository extends JpaRepository<AvailabilityEntity, Long> {
    List<AvailabilityEntity> findByEmployeeIdAndDate(Long employeeId, LocalDate date);

    boolean existsByEmployeeIdAndDateAndStartTime(Long employeeId, LocalDate date, LocalTime startTime);

    @Query("""
        SELECT DISTINCT a.date
        FROM AvailabilityEntity a
        WHERE a.employee.id = :employeeId
          AND a.date > :from
          AND a.date <= :to
        ORDER BY a.date
    """)
    
    List<LocalDate> findAvailableDatesByEmployee(
            @Param("employeeId") Long employeeId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("""
        SELECT DISTINCT a.date
        FROM AvailabilityEntity a
        JOIN a.employee e
        JOIN e.services s
        WHERE s.id = :serviceId
          AND e.isActive = true
          AND a.date > :from
          AND a.date <= :to
        ORDER BY a.date
    """)

    List<LocalDate> findAvailableDatesByService(
            @Param("serviceId") Long serviceId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    List<AvailabilityEntity> findByEmployeeIdAndDateBetween(Long employeeId, LocalDate from, LocalDate to);

}
