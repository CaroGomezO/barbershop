package com.example.barbershop.infrastructure.adapter.out.persistence.jpa;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.barbershop.infrastructure.adapter.out.persistence.entity.AppointmentEntity;


@Repository
public interface BarberAgendaJpaRepository extends JpaRepository<AppointmentEntity, Long> {

    
    @Query("""
        SELECT a
          FROM AppointmentEntity a
               JOIN FETCH a.client c
               JOIN FETCH c.user cu
               JOIN FETCH a.details d
               JOIN FETCH d.service s
         WHERE a.employee.id = :barberoId
           AND a.date BETWEEN :desde AND :hasta
           AND a.date >= CURRENT_DATE
         ORDER BY a.date ASC, a.startTime ASC
    """)
    List<AppointmentEntity> findAgendaByBarberoAndRango(
            @Param("barberoId") Long barberoId,
            @Param("desde")     LocalDate desde,
            @Param("hasta")     LocalDate hasta
    );

    
    @Query("""
        SELECT a
          FROM AppointmentEntity a
               JOIN FETCH a.client c
               JOIN FETCH c.user cu
               JOIN FETCH a.details d
               JOIN FETCH d.service s
         WHERE a.employee.id = :barberoId
           AND a.date = :fecha
           AND a.date >= CURRENT_DATE
         ORDER BY a.startTime ASC
    """)
    List<AppointmentEntity> findAgendaByBarberoAndFecha(
            @Param("barberoId") Long barberoId,
            @Param("fecha")     LocalDate fecha
    );
}
