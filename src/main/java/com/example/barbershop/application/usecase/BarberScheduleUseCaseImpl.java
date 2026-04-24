package com.example.barbershop.application.usecase;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.barbershop.application.dto.BarberScheduleResponse;
import com.example.barbershop.application.dto.BarberScheduleResponse.AppointmentInfoDto;
import com.example.barbershop.application.dto.BarberScheduleResponse.DayScheduleDto;
import com.example.barbershop.application.dto.BarberScheduleResponse.SlotDto;
import com.example.barbershop.application.port.in.BarberScheduleUseCase;
import com.example.barbershop.application.port.out.AppointmentRepositoryPort;
import com.example.barbershop.application.port.out.AvailabilityRepositoryPort;
import com.example.barbershop.application.port.out.EmployeeRepositoryPort;
import com.example.barbershop.domain.exception.EmployeeNotFoundException;
import com.example.barbershop.domain.model.Appointment;
import com.example.barbershop.domain.model.Availability;
import com.example.barbershop.domain.model.Employee;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BarberScheduleUseCaseImpl implements BarberScheduleUseCase{
    private final EmployeeRepositoryPort employeeRepository;
    private final AvailabilityRepositoryPort availabilityRepository;
    private final AppointmentRepositoryPort appointmentRepository;

    @Override
    public BarberScheduleResponse getBarberSchedule(Long employeeId, LocalDate from, LocalDate to) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(EmployeeNotFoundException::new);
        
        List<Availability> availabilities = availabilityRepository.findByEmployeeIdAndDateBetween(employeeId, from, to);

        List<Appointment> appointments = appointmentRepository.findByEmployeeIdAndDateBetween(employeeId, from, to);

        List<DayScheduleDto> days = from.datesUntil(to.plusDays(1))
                .map(date -> buildDay(date, availabilities, appointments))
                .filter(day -> !day.getSlots().isEmpty())
                .collect(Collectors.toList());
        
        return BarberScheduleResponse.builder()
                .employeeId(employee.getId())
                .names(employee.getNames())
                .lastNames(employee.getLastNames())
                .days(days)
                .build();
    }

    private DayScheduleDto buildDay(LocalDate date, List<Availability> availabilities, List<Appointment> appointments) {
        List<Availability> dayAvailabilities = availabilities.stream()
                .filter(a -> a.getDate().equals(date))
                .collect(Collectors.toList());
        
        List<Appointment> dayAppointments = appointments.stream()
                .filter(a -> a.getDate().equals(date))
                .collect(Collectors.toList());
        
        List<SlotDto> slots = new ArrayList<>();

        for (Availability availability : dayAvailabilities) {
            LocalTime current = availability.getStartTime();

            int minDuration = availability.getEmployee().getServices().stream()
                    .mapToInt(s -> s.getDurationMinutes())
                    .min()
                    .orElse(15);
            
            while (current.isBefore(availability.getEndTime())) {
                LocalTime slotEnd = current.plusMinutes(minDuration);

                if (slotEnd.isAfter(availability.getEndTime())) {
                    break;
                }

                final LocalTime slotStart = current;

                Appointment match = dayAppointments.stream()
                        .filter(a -> !a.getStartTime().isAfter(slotStart) && a.getEndTime().isAfter(slotStart))
                        .findFirst()
                        .orElse(null);
                
                if (match != null) {
                    slots.add(SlotDto.builder()
                            .startTime(match.getStartTime().toString())
                            .endTime(match.getEndTime().toString())
                            .status("OCUPADO")
                            .appointment(AppointmentInfoDto.builder()
                                    .appointmentId(match.getId())
                                    .clientNames(match.getClient().getNames())
                                    .clientLastNames(match.getClient().getLastNames())
                                    .services(match.getDetails().stream()
                                            .map(d -> d.getService().getName())
                                            .collect(Collectors.toList()))
                                    .totalPrice(match.getTotalPrice().toString())
                                    .build())
                            .build());

                    current = match.getEndTime();
                } else {
                    slots.add(SlotDto.builder()
                            .startTime(slotStart.toString())
                            .endTime(slotEnd.toString())
                            .status("LIBRE")
                            .appointment(null)
                            .build());
                    current = slotEnd;
                }
            }  
        }

        return DayScheduleDto.builder()
                .date(date.toString())
                .slots(slots)
                .build();
    }
}