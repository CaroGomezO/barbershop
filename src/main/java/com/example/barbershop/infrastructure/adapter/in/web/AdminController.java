package com.example.barbershop.infrastructure.adapter.in.web;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.barbershop.application.dto.BarberScheduleResponse;
import com.example.barbershop.application.dto.CreateServiceRequest;
import com.example.barbershop.application.dto.RegisterEmployeeRequest;
import com.example.barbershop.application.dto.RegisterEmployeeResponse;
import com.example.barbershop.application.dto.ServiceResponse;
import com.example.barbershop.application.dto.UpdateEmployeeRequest;
import com.example.barbershop.application.dto.UpdateEmployeeResponse;
import com.example.barbershop.application.dto.WorkScheduleRequest;
import com.example.barbershop.application.dto.WorkScheduleResponse;
import com.example.barbershop.application.port.in.BarberScheduleUseCase;
import com.example.barbershop.application.port.in.ManageServicesUseCase;
import com.example.barbershop.application.port.in.RegisterEmployeeUseCase;
import com.example.barbershop.application.port.in.UpdateEmployeeUseCase;
import com.example.barbershop.application.port.in.WorkScheduleUseCase;
import com.example.barbershop.application.port.out.ServiceRepositoryPort;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final RegisterEmployeeUseCase registerEmployeeUseCase;
    private final ServiceRepositoryPort serviceRepository;
    private final ManageServicesUseCase manageServicesUseCase;
    private final BarberScheduleUseCase barberScheduleUseCase;
    private final WorkScheduleUseCase workScheduleUseCase;
    private final UpdateEmployeeUseCase updateEmployeeUseCase;

    @PostMapping("/employees")
    public ResponseEntity<RegisterEmployeeResponse> registerEmployee(
            @Valid @RequestBody RegisterEmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registerEmployeeUseCase.register(request));
    }

    @GetMapping("/employees/{employeeId}/schedule")
    public ResponseEntity<BarberScheduleResponse> getSchedule(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(barberScheduleUseCase.getBarberSchedule(employeeId, from, to));
    }
    
    @PostMapping("/services")
    public ResponseEntity<ServiceResponse> createService(
            @Valid @RequestBody CreateServiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(manageServicesUseCase.createService(request));
    }

    @PostMapping("/employees/{employeeId}/schedules")
    public ResponseEntity<WorkScheduleResponse> createSchedule(
            @PathVariable Long employeeId,
            @Valid @RequestBody WorkScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(workScheduleUseCase.create(employeeId, request));
    }

    @PutMapping("/employees/schedules/{scheduleId}")
    public ResponseEntity<WorkScheduleResponse> updateSchedule(
            @PathVariable Long scheduleId,
            @Valid @RequestBody WorkScheduleRequest request) {
        return ResponseEntity.ok(workScheduleUseCase.update(scheduleId, request));
    }

    @DeleteMapping("/employees/schedules/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long scheduleId) {
        workScheduleUseCase.delete(scheduleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employees/{employeeId}/schedules")
    public ResponseEntity<List<WorkScheduleResponse>> getSchedules(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(workScheduleUseCase.findByEmployee(employeeId));
    }

    @PatchMapping("/employees/{employeeId}/update")
    public ResponseEntity<UpdateEmployeeResponse> updateEmployee(
            @PathVariable Long employeeId,
            @Valid @RequestBody UpdateEmployeeRequest request) {
        return ResponseEntity.ok(updateEmployeeUseCase.update(employeeId, request));
    }
}
