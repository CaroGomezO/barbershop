package com.example.barbershop.application.usecase;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.barbershop.application.dto.AgendaItemResponse;
import com.example.barbershop.application.dto.BarberAgendaRequest;
import com.example.barbershop.application.dto.BarberAgendaRequest.Navegacion;
import com.example.barbershop.application.dto.BarberAgendaResponse;
import com.example.barbershop.application.port.in.BarberAgendaUseCase;
import com.example.barbershop.application.port.out.BarberAgendaRepositoryPort;
import com.example.barbershop.application.security.UserContext;
import com.example.barbershop.domain.model.AgendaItem;
import com.example.barbershop.domain.model.ViewType;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class BarberAgendaUseCaseImpl implements BarberAgendaUseCase {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final BarberAgendaRepositoryPort agendaRepository;
    private final AgendaPdfExporter pdfExporter;

    

    @Override
    public BarberAgendaResponse obtenerAgendaFutura(BarberAgendaRequest request, UserContext context) {

        Long barberoId = context.getUserId();
        LocalDate hoy  = LocalDate.now();

        
        LocalDate fechaBase = (request.getFecha() != null) ? request.getFecha() : hoy;

        
        fechaBase = aplicarNavegacion(fechaBase, request.getVista(), request.getNavegacion());

        
        DateRange rango = calcularRango(request.getVista(), fechaBase, hoy);

        
        List<AgendaItem> items = agendaRepository
                .findAgendaByBarberoAndRango(barberoId, rango.desde, rango.hasta);

        List<AgendaItemResponse> citas = items.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        
        Map<String, List<AgendaItemResponse>> citasPorDia = citas.stream()
                .collect(Collectors.groupingBy(
                        AgendaItemResponse::getFecha,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        LocalDate fechaAnterior  = retrocederPeriodo(fechaBase, request.getVista());
        LocalDate fechaSiguiente = avanzarPeriodo(fechaBase, request.getVista());

        
        String mensaje = citas.isEmpty()
                ? mensajeVacio(request.getVista(), rango)
                : null;

        return BarberAgendaResponse.builder()
                .vista(request.getVista().name())
                .fechaDesde(rango.desde.format(DATE_FMT))
                .fechaHasta(rango.hasta.format(DATE_FMT))
                .totalCitas(citas.size())
                .citas(citas)
                .citasPorDia(citasPorDia)
                .periodoAnterior(fechaAnterior.format(DATE_FMT))
                .periodoSiguiente(fechaSiguiente.format(DATE_FMT))
                .mensaje(mensaje)
                .build();
    }

    @Override
    public byte[] exportarAgendaPdf(BarberAgendaRequest request, UserContext context) {
        BarberAgendaResponse agenda = obtenerAgendaFutura(request, context);
        return pdfExporter.exportar(agenda);
    }

    
    private LocalDate aplicarNavegacion(LocalDate fechaBase, ViewType vista, Navegacion navegacion) {
        if (navegacion == null) return fechaBase;
        return switch (navegacion) {
            case ANTERIOR  -> retrocederPeriodo(fechaBase, vista);
            case SIGUIENTE -> avanzarPeriodo(fechaBase, vista);
        };
    }

    private LocalDate retrocederPeriodo(LocalDate fecha, ViewType vista) {
        return switch (vista) {
            case DIARIA   -> fecha.minusDays(1);
            case SEMANAL  -> fecha.minusWeeks(1);
            case MENSUAL  -> fecha.minusMonths(1);
        };
    }

    private LocalDate avanzarPeriodo(LocalDate fecha, ViewType vista) {
        return switch (vista) {
            case DIARIA   -> fecha.plusDays(1);
            case SEMANAL  -> fecha.plusWeeks(1);
            case MENSUAL  -> fecha.plusMonths(1);
        };
    }

   

    private DateRange calcularRango(ViewType vista, LocalDate fechaBase, LocalDate hoy) {
        return switch (vista) {
            case DIARIA -> {
                // Si la fecha es pasada, mostramos hoy (sin mensaje de error; es un ajuste silencioso)
                LocalDate dia = fechaBase.isBefore(hoy) ? hoy : fechaBase;
                yield new DateRange(dia, dia);
            }
            case SEMANAL -> {
                LocalDate inicioSemana = fechaBase.with(DayOfWeek.MONDAY);
                LocalDate finSemana   = fechaBase.with(DayOfWeek.SUNDAY);
                LocalDate desde = inicioSemana.isBefore(hoy) ? hoy : inicioSemana;
                yield new DateRange(desde, finSemana);
            }
            case MENSUAL -> {
                LocalDate inicioMes = fechaBase.with(TemporalAdjusters.firstDayOfMonth());
                LocalDate finMes    = fechaBase.with(TemporalAdjusters.lastDayOfMonth());
                LocalDate desde = inicioMes.isBefore(hoy) ? hoy : inicioMes;
                yield new DateRange(desde, finMes);
            }
        };
    }

   

    private AgendaItemResponse toResponse(AgendaItem item) {
        return AgendaItemResponse.builder()
                .citaId(item.getAppointmentId())
                .nombreCliente(item.getClientFullName())
                .servicio(item.getServiceName())
                .fecha(item.getDate().format(DATE_FMT))
                .hora(item.getStartTime().format(TIME_FMT))
                .horaFin(item.getEndTime().format(TIME_FMT))
                .estado(item.getStatus().name())
                .cancelada(item.isCancelled())
                .build();
    }

    

    private String mensajeVacio(ViewType vista, DateRange rango) {
        return switch (vista) {
            case DIARIA  -> "No tienes citas programadas para el día "
                             + rango.desde.format(DATE_FMT) + ".";
            case SEMANAL -> "No tienes citas programadas para la semana del "
                             + rango.desde.format(DATE_FMT)
                             + " al " + rango.hasta.format(DATE_FMT) + ".";
            case MENSUAL -> "No tienes citas programadas para este mes.";
        };
    }

   

    private record DateRange(LocalDate desde, LocalDate hasta) {}
}
