package com.example.barbershop.infrastructure.adapter.in.web;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.barbershop.application.dto.BarberScheduleResponse;
import com.example.barbershop.application.dto.RegisterEmployeeRequest;
import com.example.barbershop.application.dto.RegisterEmployeeResponse;
import com.example.barbershop.application.port.in.BarberScheduleUseCase;
import com.example.barbershop.application.port.in.RegisterEmployeeUseCase;
import com.example.barbershop.application.port.out.ServiceRepositoryPort;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final RegisterEmployeeUseCase registerEmployeeUseCase;
    private final ServiceRepositoryPort serviceRepository;
    private final BarberScheduleUseCase barberScheduleUseCase;

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
}
