package com.example.barbershop.infrastructure.adapter.out.persistence.jpa;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.barbershop.domain.model.AppointmentStatus;
import com.example.barbershop.infrastructure.adapter.out.persistence.entity.AppointmentEntity;

@Repository
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

    @Query("""
        SELECT a FROM AppointmentEntity a
        WHERE a.employee.id = :employeeId
          AND a.date BETWEEN :from AND :to
          AND a.status = 'CONFIRMADA'
    """)
    List<AppointmentEntity> findConfirmedByEmployeeAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Modifying
    @Query("UPDATE AppointmentEntity a SET a.status = :status WHERE a.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") AppointmentStatus status);

    List<AppointmentEntity> findByDate(LocalDate date);

    List<AppointmentEntity> findByStatus(String status);

    List<AppointmentEntity> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<AppointmentEntity> findByEmployeeId(Long employeeId);
}