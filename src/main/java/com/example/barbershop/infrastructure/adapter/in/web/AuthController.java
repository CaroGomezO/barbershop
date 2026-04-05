package com.example.barbershop.infrastructure.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.barbershop.application.dto.ChangePasswordRequest;
import com.example.barbershop.application.dto.LoginRequest;
import com.example.barbershop.application.dto.LoginResponse;
import com.example.barbershop.application.dto.RegisterClientRequest;
import com.example.barbershop.application.dto.RegisterClientResponse;
import com.example.barbershop.application.port.in.ChangePasswordUseCase;
import com.example.barbershop.application.port.in.LoginUseCase;
import com.example.barbershop.application.port.in.RegisterClientUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final LoginUseCase loginUseCase;
    private final RegisterClientUseCase registerClientUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginUseCase.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterClientResponse> register(
            @Valid @RequestBody RegisterClientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registerClientUseCase.register(request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        changePasswordUseCase.changePassword(
                authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }
}
