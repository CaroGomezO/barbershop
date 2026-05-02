package com.example.barbershop.application.port.in;

import java.time.LocalDate;

import com.example.barbershop.application.dto.BarberAgendaRequest;
import com.example.barbershop.application.dto.BarberAgendaResponse;
import com.example.barbershop.application.security.UserContext;


public interface BarberAgendaUseCase {

    BarberAgendaResponse obtenerAgendaFutura(BarberAgendaRequest request, UserContext context);
    byte[] exportarAgendaPdf(BarberAgendaRequest request, UserContext context);
}
