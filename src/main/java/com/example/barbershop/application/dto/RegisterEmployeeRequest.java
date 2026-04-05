package com.example.barbershop.application.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegisterEmployeeRequest {
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 15, message = "La contraseña debe tener entre 6 y 15 caracteres")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", message = "La contraseña debe tener mayúscula, minúscula y número")
    private String password;

    @NotBlank(message = "El documento es obligatorio")
    private String documentNumber;

    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El nombre no puede contener caracteres numéricos")
    private String names;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "Los apellidos no pueden contener caracteres numéricos")
    private String lastNames;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]{7,15}$", message = "El teléfono debe contener entre 7 y 15 dígitos")
    private String phoneNumber;

    @NotBlank(message = "La dirección es obligatoria")
    private String address;

    @NotNull(message = "Debes asignar al menos un servicio")
    @Size(min = 1, message = "Debes asignar al menos un servicio")
    private List<Long> serviceIds;
}
