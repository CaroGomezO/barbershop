package com.example.barbershop.application.port.out;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

import com.example.barbershop.domain.model.WorkSchedule;

public interface WorkScheduleRepositoryPort {
    WorkSchedule save(WorkSchedule schedule);
    Optional<WorkSchedule> findById(Long id);
    List<WorkSchedule> findByEmployeeId(Long employeeId);
    boolean existsByEmployeeIdAndDayOfWeek(Long employeeId, DayOfWeek dayOfWeek);
    boolean existsByEmployeeIdAndDayOfWeekAndIdNot(Long employeeId, DayOfWeek dayOfWeek, Long excludeId);
    void deleteById(Long id);
}
