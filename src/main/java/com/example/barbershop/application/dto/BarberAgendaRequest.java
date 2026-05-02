package com.example.barbershop.application.dto;

import java.time.LocalDate;

import com.example.barbershop.domain.model.ViewType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;



@Schema(description = "Filtros para consultar la agenda futura del barbero")
public class BarberAgendaRequest {

    @NotNull(message = "El tipo de vista es obligatorio")
    @Schema(
        description = "Tipo de vista de la agenda",
        allowableValues = {"DIARIA", "SEMANAL", "MENSUAL"},
        example = "SEMANAL"
    )
    private ViewType vista;

    @Schema(
        description = "Fecha base para la consulta. "
            + "En vista DIARIA: muestra solo ese día. "
            + "En vista SEMANAL: muestra la semana que contiene esa fecha. "
            + "En vista MENSUAL: muestra todo el mes. "
            + "Si se omite, se usa la fecha actual.",
        example = "2025-05-10"
    )
    private LocalDate fecha;

    @Schema(
        description = "Navegación entre periodos respecto a 'fecha'. "
            + "ANTERIOR: retrocede un periodo (día/semana/mes). "
            + "SIGUIENTE: avanza un periodo. "
            + "Si se omite, se muestra el periodo de 'fecha' sin desplazamiento.",
        allowableValues = {"ANTERIOR", "SIGUIENTE"},
        example = "SIGUIENTE"
    )
    private Navegacion navegacion;

    public ViewType getVista() { return vista; }
    public void setVista(ViewType vista) { this.vista = vista; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Navegacion getNavegacion() { return navegacion; }
    public void setNavegacion(Navegacion navegacion) { this.navegacion = navegacion; }

    /** Dirección de navegación entre periodos de la agenda. */
    public enum Navegacion {
        ANTERIOR,
        SIGUIENTE
    }
}

