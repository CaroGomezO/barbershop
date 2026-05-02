package com.example.barbershop.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ítem de cita en la agenda del barbero")
public class AgendaItemResponse {

    @Schema(example = "42")
    private Long citaId;

    @Schema(example = "Carlos Ramírez")
    private String nombreCliente;

    @Schema(example = "Corte + Barba")
    private String servicio;

    @Schema(example = "2025-05-12")
    private String fecha;

    @Schema(example = "10:30")
    private String hora;

    @Schema(example = "11:00")
    private String horaFin;

    @Schema(
        description = "Estado de la cita: PENDIENTE, CONFIRMADA o CANCELADA",
        example = "CONFIRMADA"
    )
    private String estado;

   
    @Schema(
        description = "Indica si la cita fue cancelada; útil para diferenciación visual",
        example = "false"
    )
    private boolean cancelada;
}
