package com.example.barbershop.infrastructure.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.barbershop.application.dto.RegisterEmployeeRequest;
import com.example.barbershop.application.dto.RegisterEmployeeResponse;
import com.example.barbershop.application.port.in.RegisterEmployeeUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final RegisterEmployeeUseCase registerEmployeeUseCase;

    @PostMapping("/employees")
    public ResponseEntity<RegisterEmployeeResponse> registerEmployee(
            @Valid @RequestBody RegisterEmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registerEmployeeUseCase.register(request));
    }
}
