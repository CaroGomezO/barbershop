package com.example.barbershop.application.port.out;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.example.barbershop.domain.model.Availability;

public interface AvailabilityRepositoryPort {
    Availability save(Availability availability);
    List<Availability> findByEmployeeIdAndDate(Long employeeId, LocalDate date);
    List<LocalDate> findAvailableDatesByEmployee(Long employeeId, LocalDate from, LocalDate to);
    List<LocalDate> findAvailableDatesByService(Long serviceId, LocalDate from, LocalDate to);
    boolean existsByEmployeeIdAndDateAndStartTime(Long employeeId, LocalDate date, LocalTime startTime);
    List<Availability> findByEmployeeIdAndDateBetween(Long employeeId, LocalDate from, LocalDate to);
}
