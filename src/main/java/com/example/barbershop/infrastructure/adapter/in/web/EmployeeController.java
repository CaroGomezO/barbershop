package com.example.barbershop.infrastructure.adapter.in.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.barbershop.application.dto.AvailabilityRequest;
import com.example.barbershop.application.dto.AvailabilityResponse;
import com.example.barbershop.application.port.in.ScheduleAvailabilityUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {
    private final ScheduleAvailabilityUseCase scheduleAvailabilityUseCase;

    @PostMapping("/availability")
    public ResponseEntity<List<AvailabilityResponse>> schedule(
            @Valid @RequestBody AvailabilityRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(scheduleAvailabilityUseCase.schedule(
                        authentication.getName(), request));
    }
}
