package com.example.barbershop.application.port.out;

import java.time.LocalDate;
import java.util.List;

import com.example.barbershop.domain.model.AgendaItem;


public interface BarberAgendaRepositoryPort {

    List<AgendaItem> findAgendaByBarberoAndRango(Long barberoId, LocalDate desde, LocalDate hasta);
    List<AgendaItem> findAgendaByBarberoAndFecha(Long barberoId, LocalDate fecha);
}
