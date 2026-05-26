package com.example.barbershop.infrastructure.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.barbershop.application.dto.UpdateClientRequest;
import com.example.barbershop.application.dto.UpdateClientResponse;
import com.example.barbershop.application.port.in.UpdateClientUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {
    private final UpdateClientUseCase updateClientUseCase;

    @PatchMapping("/{clientId}/update")
    public ResponseEntity<UpdateClientResponse> updateClient(
            @PathVariable Long clientId,
            @Valid @RequestBody UpdateClientRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(
                updateClientUseCase.update(
                        clientId,
                        authentication.getName(),
                        request));
    }
}
