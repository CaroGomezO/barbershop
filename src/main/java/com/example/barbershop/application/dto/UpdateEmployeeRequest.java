package com.example.barbershop.application.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateEmployeeRequest {

    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$",
             message = "El nombre no puede contener caracteres numéricos")
    @Schema(description = "Dejar vacío para no modificar", example = "Carlos Alberto")
    private String names;

    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$",
             message = "Los apellidos no pueden contener caracteres numéricos")
    @Schema(description = "Dejar vacío para no modificar", example = "Gómez Ruiz")
    private String lastNames;

    @Pattern(regexp = "^[0-9]{7,15}$",
             message = "El teléfono debe contener entre 7 y 15 dígitos")
    @Schema(description = "Dejar vacío para no modificar", example = "3009876543")
    private String phone;

    @Schema(description = "Dejar vacío para no modificar", example = "Carrera 45 #20-10")
    private String address;

    @Size(min = 1, message = "Debes asignar al menos un servicio")
    @Schema(description = "Dejar vacío para no modificar", example = "[1, 3, 7]")
    private List<Long> serviceIds;
}
