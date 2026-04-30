package com.example.barbershop.application.port.in;

import java.time.LocalDate;
import java.util.List;

import com.example.barbershop.application.dto.AppointmentResponse;

public interface GetAppointmentsUseCase {
    List<AppointmentResponse> getAppointmentsByDate(LocalDate date);
    List<AppointmentResponse> getAppointmentsByStatus(String status);
    List<AppointmentResponse> getAppointmentsByDateRange(LocalDate startDate, LocalDate endDate);
    AppointmentResponse getAppointmentById(Long id);
    List<AppointmentResponse> getAppointmentsByEmployee(Long employeeId);
}