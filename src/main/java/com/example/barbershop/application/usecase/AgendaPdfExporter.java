package com.example.barbershop.application.usecase;

import com.example.barbershop.application.dto.BarberAgendaResponse;


public interface AgendaPdfExporter {

    byte[] exportar(BarberAgendaResponse agenda);
}
