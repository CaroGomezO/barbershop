package com.example.barbershop.infrastructure.adapter.out.persistence.jpa;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.barbershop.infrastructure.adapter.out.persistence.entity.WorkScheduleEntity;

public interface WorkScheduleJpaRepository extends JpaRepository<WorkScheduleEntity, Long> {
    List<WorkScheduleEntity> findByEmployeeId(Long employeeId);

    boolean existsByEmployeeIdAndDayOfWeek(Long employeeId, DayOfWeek dayOfWeek);

    boolean existsByEmployeeIdAndDayOfWeekAndIdNot(Long employeeId, DayOfWeek dayOfWeek, Long id);

    Optional<WorkScheduleEntity> findByEmployeeIdAndDayOfWeek(Long employeeId, DayOfWeek dayOfWeek);
}
