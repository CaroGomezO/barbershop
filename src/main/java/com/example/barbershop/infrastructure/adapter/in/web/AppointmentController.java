package com.example.barbershop.infrastructure.adapter.in.web;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.barbershop.application.dto.AppointmentSummaryResponse;
import com.example.barbershop.application.dto.AvailableDatesResponse;
import com.example.barbershop.application.dto.ConfirmAppointmentRequest;
import com.example.barbershop.application.dto.ConfirmAppointmentResponse;
import com.example.barbershop.application.dto.EmployeeAvailabilityResponse;
import com.example.barbershop.application.dto.ServiceAvailabilityResponse;
import com.example.barbershop.application.dto.SlotResponse;
import com.example.barbershop.application.port.in.AppointmentUseCase;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentUseCase appointmentUseCase;

    @GetMapping("/services")
    public ResponseEntity<List<ServiceAvailabilityResponse>> getServices() {
        return ResponseEntity.ok(appointmentUseCase.getServicesWithAvailability());
    }

    @GetMapping("/services/{serviceId}/employees")
    public ResponseEntity<List<EmployeeAvailabilityResponse>> getEmployees(
            @PathVariable Long serviceId) {
        return ResponseEntity.ok(appointmentUseCase.getAvailableEmployees(serviceId));
    }

    @GetMapping("/employees/{employeeId}/dates")
    public ResponseEntity<AvailableDatesResponse> getDates(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(appointmentUseCase.getAvailableDates(employeeId));
    }

    @GetMapping("/employees/{employeeId}/slots")
    public ResponseEntity<List<SlotResponse>> getSlots(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam List<Long> serviceIds) {
        return ResponseEntity.ok(appointmentUseCase.getAvailableSlots(employeeId, date, serviceIds));
    }

    @GetMapping("/summary")
    public ResponseEntity<AppointmentSummaryResponse> getSummary(
            @RequestParam Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            @Schema(type = "string", format = "time", example = "11:30:00") LocalTime startTime,
            @RequestParam List<Long> serviceIds) {
        return ResponseEntity.ok(
                appointmentUseCase.getSummary(employeeId, date, startTime, serviceIds));
    }

    @PostMapping("/confirm")
    public ResponseEntity<ConfirmAppointmentResponse> confirm(
            @Valid @RequestBody ConfirmAppointmentRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointmentUseCase.confirm(
                        authentication.getName(), request));
    }
}
