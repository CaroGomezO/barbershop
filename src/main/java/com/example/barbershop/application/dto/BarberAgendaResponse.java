package com.example.barbershop.application.dto;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Agenda futura del barbero con metadatos de la vista")
public class BarberAgendaResponse {

    @Schema(description = "Tipo de vista consultada", example = "SEMANAL")
    private String vista;

    @Schema(description = "Fecha de inicio del rango consultado", example = "2025-05-12")
    private String fechaDesde;

    @Schema(description = "Fecha de fin del rango consultado", example = "2025-05-18")
    private String fechaHasta;

    @Schema(description = "Total de citas en el rango", example = "5")
    private int totalCitas;

    @Schema(description = "Lista plana de citas ordenadas por fecha y hora")
    private List<AgendaItemResponse> citas;

    
    @Schema(
        description = "Citas agrupadas por fecha (yyyy-MM-dd). "
            + "Útil para vistas semanal y mensual.",
        example = "{\"2025-05-13\": [...], \"2025-05-14\": [...]}"
    )
    private Map<String, List<AgendaItemResponse>> citasPorDia;

   
    @Schema(description = "Fecha base para navegar al periodo anterior", example = "2025-05-05")
    private String periodoAnterior;

    @Schema(description = "Fecha base para navegar al periodo siguiente", example = "2025-05-19")
    private String periodoSiguiente;

    @Schema(
        description = "Mensaje informativo cuando no hay citas o se aplica algún aviso",
        example = "No tienes citas programadas para esta semana."
    )
    private String mensaje;
}
