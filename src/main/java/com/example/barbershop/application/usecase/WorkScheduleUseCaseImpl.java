package com.example.barbershop.application.usecase;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.barbershop.application.dto.WorkScheduleRequest;
import com.example.barbershop.application.dto.WorkScheduleResponse;
import com.example.barbershop.application.port.in.WorkScheduleUseCase;
import com.example.barbershop.application.port.out.EmployeeRepositoryPort;
import com.example.barbershop.application.port.out.WorkScheduleRepositoryPort;
import com.example.barbershop.domain.exception.EmployeeNotFoundException;
import com.example.barbershop.domain.exception.InvalidScheduleException;
import com.example.barbershop.domain.exception.OverlappingScheduleException;
import com.example.barbershop.domain.exception.WorkScheduleNotFoundException;
import com.example.barbershop.domain.model.Employee;
import com.example.barbershop.domain.model.WorkSchedule;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkScheduleUseCaseImpl implements WorkScheduleUseCase{
    private static final LocalTime OPENING = LocalTime.of(8, 0);
    private static final LocalTime CLOSING = LocalTime.of(19, 0);

    private final WorkScheduleRepositoryPort workScheduleRepository;
    private final EmployeeRepositoryPort employeeRepository;

    @Override
    @Transactional
    public WorkScheduleResponse create(Long employeeId, WorkScheduleRequest request) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(EmployeeNotFoundException::new);

        validateTimes(request.getStartTime(), request.getEndTime());

        if (workScheduleRepository.existsByEmployeeIdAndDayOfWeek(
                employeeId, request.getDayOfWeek())) {
            throw new OverlappingScheduleException();
        }

        WorkSchedule schedule = WorkSchedule.builder()
                .employee(employee)
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        return toResponse(workScheduleRepository.save(schedule));
    }

    @Override
    @Transactional
    public WorkScheduleResponse update(Long scheduleId, WorkScheduleRequest request) {

        WorkSchedule existing = workScheduleRepository.findById(scheduleId)
                .orElseThrow(WorkScheduleNotFoundException::new);

        validateTimes(request.getStartTime(), request.getEndTime());

        // Verificar solapamiento excluyendo el registro actual
        if (workScheduleRepository.existsByEmployeeIdAndDayOfWeekAndIdNot(
                existing.getEmployee().getId(),
                request.getDayOfWeek(),
                scheduleId)) {
            throw new OverlappingScheduleException();
        }

        existing.setDayOfWeek(request.getDayOfWeek());
        existing.setStartTime(request.getStartTime());
        existing.setEndTime(request.getEndTime());

        return toResponse(workScheduleRepository.save(existing));
    }

    @Override
    @Transactional
    public void delete(Long scheduleId) {
        if (workScheduleRepository.findById(scheduleId).isEmpty()) {
            throw new WorkScheduleNotFoundException();
        }
        workScheduleRepository.deleteById(scheduleId);
    }

    @Override
    public List<WorkScheduleResponse> findByEmployee(Long employeeId) {
        if (employeeRepository.findById(employeeId).isEmpty()) {
            throw new EmployeeNotFoundException();
        }
        return workScheduleRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void validateTimes(LocalTime start, LocalTime end) {
        if (!start.isBefore(end)) {
            throw new InvalidScheduleException(
                    "La hora de inicio debe ser anterior a la hora de fin");
        }
        if (start.isBefore(OPENING)) {
            throw new InvalidScheduleException(
                    "La hora de inicio no puede ser antes de las 08:00");
        }
        if (end.isAfter(CLOSING)) {
            throw new InvalidScheduleException(
                    "La hora de fin no puede ser después de las 19:00");
        }
    }

    private WorkScheduleResponse toResponse(WorkSchedule ws) {
        return WorkScheduleResponse.builder()
                .id(ws.getId())
                .employeeId(ws.getEmployee().getId())
                .employeeNames(ws.getEmployee().getNames())
                .employeeLastNames(ws.getEmployee().getLastNames())
                .dayOfWeek(ws.getDayOfWeek())
                .startTime(ws.getStartTime())
                .endTime(ws.getEndTime())
                .build();
    }
}
