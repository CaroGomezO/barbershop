package com.example.barbershop.infrastructure.adapter.out.persistence.adapter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.barbershop.application.port.out.WorkScheduleRepositoryPort;
import com.example.barbershop.domain.model.WorkSchedule;
import com.example.barbershop.infrastructure.adapter.out.persistence.entity.WorkScheduleEntity;
import com.example.barbershop.infrastructure.adapter.out.persistence.jpa.WorkScheduleJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WorkScheduleRepositoryAdapter implements WorkScheduleRepositoryPort  {
    private final WorkScheduleJpaRepository jpaRepository;
    private final EmployeeRepositoryAdapter employeeAdapter;

    @Override
    public WorkSchedule save(WorkSchedule schedule) {
        WorkScheduleEntity entity;

        if (schedule.getId() != null) {
            entity = jpaRepository.findById(schedule.getId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Jornada no encontrada"));
            entity.setDayOfWeek(schedule.getDayOfWeek());
            entity.setStartTime(schedule.getStartTime());
            entity.setEndTime(schedule.getEndTime());
        } else {
            entity = WorkScheduleEntity.builder()
                    .employee(employeeAdapter.toEntity(
                            schedule.getEmployee().getId()))
                    .dayOfWeek(schedule.getDayOfWeek())
                    .startTime(schedule.getStartTime())
                    .endTime(schedule.getEndTime())
                    .build();
        }

        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<WorkSchedule> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<WorkSchedule> findByEmployeeId(Long employeeId) {
        return jpaRepository.findByEmployeeId(employeeId)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmployeeIdAndDayOfWeek(
            Long employeeId, DayOfWeek dayOfWeek) {
        return jpaRepository.existsByEmployeeIdAndDayOfWeek(employeeId, dayOfWeek);
    }

    @Override
    public boolean existsByEmployeeIdAndDayOfWeekAndIdNot(
            Long employeeId, DayOfWeek dayOfWeek, Long excludeId) {
        return jpaRepository.existsByEmployeeIdAndDayOfWeekAndIdNot(
                employeeId, dayOfWeek, excludeId);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private WorkSchedule toDomain(WorkScheduleEntity e) {
        return WorkSchedule.builder()
                .id(e.getId())
                .employee(employeeAdapter.toDomain(e.getEmployee()))
                .dayOfWeek(e.getDayOfWeek())
                .startTime(e.getStartTime())
                .endTime(e.getEndTime())
                .build();
    }

    @Override
    public Optional<WorkSchedule> findByEmployeeIdAndDayOfWeek(
            Long employeeId, DayOfWeek dayOfWeek) {
        return jpaRepository.findByEmployeeIdAndDayOfWeek(employeeId, dayOfWeek)
                .map(this::toDomain);
    }
}
