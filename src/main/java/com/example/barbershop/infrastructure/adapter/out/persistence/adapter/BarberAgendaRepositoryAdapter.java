package com.example.barbershop.infrastructure.adapter.out.persistence.adapter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.barbershop.application.port.out.BarberAgendaRepositoryPort;
import com.example.barbershop.domain.model.AgendaItem;
import com.example.barbershop.domain.model.AppointmentStatus;
import com.example.barbershop.infrastructure.adapter.out.persistence.entity.AppointmentDetailEntity;
import com.example.barbershop.infrastructure.adapter.out.persistence.entity.AppointmentEntity;
import com.example.barbershop.infrastructure.adapter.out.persistence.jpa.BarberAgendaJpaRepository;

import lombok.RequiredArgsConstructor;

/**
 * Adaptador de persistencia para la HU-13.
 *
 * Implementa el puerto de salida {@link BarberAgendaRepositoryPort} usando JPA.
 * Mapea las entidades JPA al objeto de dominio {@link AgendaItem}.
 */
@Component
@RequiredArgsConstructor
public class BarberAgendaRepositoryAdapter implements BarberAgendaRepositoryPort {

    private final BarberAgendaJpaRepository jpaRepository;

    @Override
    public List<AgendaItem> findAgendaByBarberoAndRango(Long barberoId,
                                                         LocalDate desde,
                                                         LocalDate hasta) {
        return jpaRepository
                .findAgendaByBarberoAndRango(barberoId, desde, hasta)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AgendaItem> findAgendaByBarberoAndFecha(Long barberoId, LocalDate fecha) {
        return jpaRepository
                .findAgendaByBarberoAndFecha(barberoId, fecha)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Mapeo Entity → Dominio
    // -------------------------------------------------------------------------

    /**
     * Convierte una AppointmentEntity en un AgendaItem de dominio.
     *
     * Para el nombre del servicio se concatenan todos los servicios del detalle
     * separados por coma. Si el detalle está vacío se muestra "Sin servicio".
     */
    private AgendaItem toDomain(AppointmentEntity entity) {
        String clienteNombre = entity.getClient().getNames()
                + " " + entity.getClient().getLastNames();

        String servicios = entity.getDetails().stream()
                .map(AppointmentDetailEntity::getService)
                .map(s -> s.getName())
                .distinct()
                .collect(Collectors.joining(", "));

        if (servicios.isBlank()) {
            servicios = "Sin servicio";
        }

        return AgendaItem.builder()
                .appointmentId(entity.getId())
                .clientFullName(clienteNombre.strip())
                .serviceName(servicios)
                .date(entity.getDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .status(entity.getStatus())
                .build();
    }
}
