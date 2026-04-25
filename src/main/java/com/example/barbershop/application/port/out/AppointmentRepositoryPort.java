package com.example.barbershop.application.port.out;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import com.example.barbershop.domain.model.Appointment;

public interface AppointmentRepositoryPort {
    Appointment save(Appointment appointment);
    boolean existsConfirmedOverlap(Long employeeId, LocalDate date, LocalTime startTime, LocalTime endTime);
    List<Appointment> findByClientEmail(String email);
    List<Appointment> findByEmployeeIdAndDateBetween(Long employeeId, LocalDate from, LocalDate to);
    Optional<Appointment> findById(Long appointmentId);
}
