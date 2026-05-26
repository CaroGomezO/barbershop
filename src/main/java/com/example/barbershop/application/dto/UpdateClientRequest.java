package com.example.barbershop.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UpdateClientRequest {
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El nombre no puede contener caracteres numéricos")
    @Schema(description = "Dejar vacío para no modificar", example = "Juan Carlos")
    private String names;

    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "Los apellidos no pueden contener caracteres numéricos")
    @Schema(description = "Dejar vacío para no modificar", example = "Pérez García")
    private String lastNames;

    @Pattern(regexp = "^[0-9]{7,15}$", message = "El teléfono debe contener entre 7 y 15 dígitos")
    @Schema(description = "Dejar vacío para no modificar", example = "3001234567")
    private String phone;
}
