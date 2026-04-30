package com.example.barbershop.application.port.out;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import com.example.barbershop.domain.model.Appointment;
import com.example.barbershop.domain.model.AppointmentStatus;

public interface AppointmentRepositoryPort {
    Appointment save(Appointment appointment);
    boolean existsConfirmedOverlap(Long employeeId, LocalDate date, LocalTime startTime, LocalTime endTime);
    List<Appointment> findByClientEmail(String email);
    List<Appointment> findByEmployeeIdAndDateBetween(Long employeeId, LocalDate from, LocalDate to);
    Optional<Appointment> findById(Long appointmentId);

    List<Appointment> findByDate(LocalDate date);
    List<Appointment> findByStatus(AppointmentStatus status);
    List<Appointment> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<Appointment> findByEmployeeId(Long employeeId);
    boolean existsById(Long id);
}
