package com.example.barbershop.application.usecase;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.barbershop.application.dto.AvailabilityRequest;
import com.example.barbershop.application.dto.AvailabilityResponse;
import com.example.barbershop.application.port.in.ScheduleAvailabilityUseCase;
import com.example.barbershop.application.port.out.AvailabilityRepositoryPort;
import com.example.barbershop.application.port.out.EmployeeRepositoryPort;
import com.example.barbershop.domain.exception.EmployeeNotFoundException;
import com.example.barbershop.domain.exception.InvalidScheduleException;
import com.example.barbershop.domain.model.Availability;
import com.example.barbershop.domain.model.Employee;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleAvailabilityUseCaseImpl implements ScheduleAvailabilityUseCase {
    private static final LocalTime OPENING = LocalTime.of(8, 0);
    private static final LocalTime CLOSING = LocalTime.of(19, 0);

    private final EmployeeRepositoryPort employeeRepository;
    private final AvailabilityRepositoryPort availabilityRepository;

    @Override
    @Transactional
    public List<AvailabilityResponse> schedule(String email, AvailabilityRequest request) {

        Employee employee = employeeRepository.findByUserEmail(email).orElseThrow(EmployeeNotFoundException::new);

        List<AvailabilityResponse> responses = new ArrayList<>();

        for (AvailabilityRequest.DaySchedule day : request.getDays()) {

            validate(day);

            boolean exists = availabilityRepository.existsByEmployeeIdAndDateAndStartTime(employee.getId(), day.getDate(), day.getStartTime());

            if (!exists) {
                Availability saved = availabilityRepository.save(
                        Availability.builder()
                                .employee(employee)
                                .date(day.getDate())
                                .startTime(day.getStartTime())
                                .endTime(day.getEndTime())
                                .build());

                responses.add(AvailabilityResponse.builder()
                        .id(saved.getId())
                        .date(saved.getDate())
                        .startTime(saved.getStartTime())
                        .endTime(saved.getEndTime())
                        .build());
            }
        }

        return responses;
    }

    private void validate(AvailabilityRequest.DaySchedule day) {
        if (day.getStartTime().isBefore(OPENING)) {
            throw new InvalidScheduleException(
                    "La hora de inicio no puede ser antes de las 08:00");
        }
        if (day.getEndTime().isAfter(CLOSING)) {
            throw new InvalidScheduleException(
                    "La hora de fin no puede ser después de las 19:00");
        }
        if (!day.getStartTime().isBefore(day.getEndTime())) {
            throw new InvalidScheduleException(
                    "La hora de inicio debe ser anterior a la hora de fin");
        }
    }
}
