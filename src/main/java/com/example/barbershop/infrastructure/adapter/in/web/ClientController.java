package com.example.barbershop.infrastructure.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "Clientes", description = "Operaciones sobre datos de clientes")
@SecurityRequirement(name = "bearerAuth")
public class ClientController {

    private final UpdateClientUseCase updateClientUseCase;

    @Operation(
        summary = "Editar cliente",
        description = "Permite al administrador o al propio cliente actualizar nombres, apellidos y teléfono. "
                    + "Solo se modifican los campos enviados en la petición."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Datos actualizados correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos con formato inválido"),
        @ApiResponse(responseCode = "403", description = "Autorización denegada"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
        @ApiResponse(responseCode = "409", description = "Teléfono ya registrado por otro cliente")
    })
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