package com.example.barbershop.infrastructure.adapter.out.persistence.jpa;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.barbershop.infrastructure.adapter.out.persistence.entity.AppointmentEntity;

public interface AppointmentJpaRepository extends JpaRepository<AppointmentEntity, Long> {
    List<AppointmentEntity> findByClientUserEmail(String email);

    @Query("""
        SELECT COUNT(a) > 0
        FROM AppointmentEntity a
        WHERE a.employee.id = :employeeId
          AND a.date = :date
          AND a.status = 'CONFIRMADA'
          AND a.startTime < :endTime
          AND a.endTime > :startTime
    """)
    
    boolean existsConfirmedOverlap(
            @Param("employeeId") Long employeeId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

}
