package com.example.barbershop.application.usecase;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.example.barbershop.application.dto.AvailabilityRequest;
import com.example.barbershop.application.dto.AvailabilityResponse;
import com.example.barbershop.application.port.in.ScheduleAvailabilityUseCase;
import com.example.barbershop.application.port.out.AvailabilityRepositoryPort;
import com.example.barbershop.application.port.out.EmployeeRepositoryPort;
import com.example.barbershop.application.port.out.WorkScheduleRepositoryPort;
import com.example.barbershop.domain.exception.EmployeeNotFoundException;
import com.example.barbershop.domain.exception.InvalidScheduleException;
import com.example.barbershop.domain.model.Availability;
import com.example.barbershop.domain.model.Employee;
import com.example.barbershop.domain.model.WorkSchedule;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleAvailabilityUseCaseImpl implements ScheduleAvailabilityUseCase {
    private static final LocalTime OPENING = LocalTime.of(8, 0);
    private static final LocalTime CLOSING = LocalTime.of(19, 0);

    private final EmployeeRepositoryPort employeeRepository;
    private final AvailabilityRepositoryPort availabilityRepository;
    private final WorkScheduleRepositoryPort workScheduleRepository;

    @Override
    @Transactional
    public List<AvailabilityResponse> schedule(String email,
                                                AvailabilityRequest request) {

        Employee employee = employeeRepository.findByUserEmail(email)
                .orElseThrow(EmployeeNotFoundException::new);

        List<AvailabilityResponse> responses = new ArrayList<>();

        for (AvailabilityRequest.DaySchedule day : request.getDays()) {

            // Validaciones básicas de horario
            validateTimes(day);

            // Validar contra la jornada laboral configurada por el admin
            DayOfWeek dayOfWeek = day.getDate().getDayOfWeek();

            WorkSchedule workSchedule = workScheduleRepository
                    .findByEmployeeIdAndDayOfWeek(employee.getId(), dayOfWeek)
                    .orElseThrow(() -> new InvalidScheduleException(
                            "No tienes jornada laboral configurada para el día "
                            + dayOfWeek.getDisplayName(
                                    TextStyle.FULL,
                                    new Locale("es", "CO"))));

            if (day.getStartTime().isBefore(workSchedule.getStartTime())) {
                throw new InvalidScheduleException(
                        "La hora de inicio no puede ser antes de las "
                        + workSchedule.getStartTime()
                        + " según tu jornada laboral");
            }

            if (day.getEndTime().isAfter(workSchedule.getEndTime())) {
                throw new InvalidScheduleException(
                        "La hora de fin no puede ser después de las "
                        + workSchedule.getEndTime()
                        + " según tu jornada laboral");
            }

            // Guardar si no existe ya ese rango
            boolean exists = availabilityRepository
                    .existsByEmployeeIdAndDateAndStartTime(
                            employee.getId(),
                            day.getDate(),
                            day.getStartTime());

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

    private void validateTimes(AvailabilityRequest.DaySchedule day) {
        if (!day.getStartTime().isBefore(day.getEndTime())) {
            throw new InvalidScheduleException(
                    "La hora de inicio debe ser anterior a la hora de fin");
        }
        if (day.getStartTime().isBefore(OPENING)) {
            throw new InvalidScheduleException(
                    "La hora de inicio no puede ser antes de las 08:00");
        }
        if (day.getEndTime().isAfter(CLOSING)) {
            throw new InvalidScheduleException(
                    "La hora de fin no puede ser después de las 19:00");
        }
    }
}
