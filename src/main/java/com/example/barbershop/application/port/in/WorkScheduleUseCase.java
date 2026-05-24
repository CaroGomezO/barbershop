package com.example.barbershop.application.port.in;

import java.util.List;

import com.example.barbershop.application.dto.WorkScheduleRequest;
import com.example.barbershop.application.dto.WorkScheduleResponse;

public interface WorkScheduleUseCase {
    WorkScheduleResponse create(Long employeeId, WorkScheduleRequest request);
    WorkScheduleResponse update(Long scheduleId, WorkScheduleRequest request);
    void delete(Long scheduleId);
    List<WorkScheduleResponse> findByEmployee(Long employeeId);
}
