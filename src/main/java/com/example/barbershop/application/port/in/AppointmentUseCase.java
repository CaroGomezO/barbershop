package com.example.barbershop.application.port.in;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.example.barbershop.application.dto.AppointmentSummaryResponse;
import com.example.barbershop.application.dto.AvailableDatesResponse;
import com.example.barbershop.application.dto.ConfirmAppointmentRequest;
import com.example.barbershop.application.dto.ConfirmAppointmentResponse;
import com.example.barbershop.application.dto.EmployeeAvailabilityResponse;
import com.example.barbershop.application.dto.ServiceAvailabilityResponse;
import com.example.barbershop.application.dto.SlotResponse;

public interface AppointmentUseCase {
    List<ServiceAvailabilityResponse> getServicesWithAvailability();
    List<EmployeeAvailabilityResponse> getAvailableEmployees(Long serviceId);
    AvailableDatesResponse getAvailableDates(Long employeeId);
    List<SlotResponse> getAvailableSlots(Long employeeId, LocalDate date, List<Long> serviceIds);
    AppointmentSummaryResponse getSummary(Long employeeId, LocalDate date, LocalTime startTime, List<Long> serviceIds);
    ConfirmAppointmentResponse confirm(String clientEmail, ConfirmAppointmentRequest request);
}
