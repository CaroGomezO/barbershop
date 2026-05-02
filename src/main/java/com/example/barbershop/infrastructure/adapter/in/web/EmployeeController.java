package com.example.barbershop.infrastructure.adapter.in.web;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.barbershop.application.dto.AvailabilityRequest;
import com.example.barbershop.application.dto.AvailabilityResponse;
import com.example.barbershop.application.dto.BarberAgendaRequest;
import com.example.barbershop.application.dto.BarberAgendaRequest.Navegacion;
import com.example.barbershop.application.dto.BarberAgendaResponse;
import com.example.barbershop.application.port.in.BarberAgendaUseCase;
import com.example.barbershop.application.port.in.ScheduleAvailabilityUseCase;
import com.example.barbershop.application.port.out.EmployeeRepositoryPort;
import com.example.barbershop.application.security.UserContext;
import com.example.barbershop.domain.model.Employee;
import com.example.barbershop.domain.model.ViewType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final ScheduleAvailabilityUseCase scheduleAvailabilityUseCase;
    private final BarberAgendaUseCase barberAgendaUseCase;
    private final EmployeeRepositoryPort employeeRepository;

    

    @PostMapping("/availability")
    public ResponseEntity<List<AvailabilityResponse>> schedule(
            @Valid @RequestBody AvailabilityRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(scheduleAvailabilityUseCase.schedule(
                        authentication.getName(), request));
    }

    

    @GetMapping("/agenda")
    public ResponseEntity<BarberAgendaResponse> obtenerAgenda(
            @RequestParam @NotNull ViewType vista,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) Navegacion navegacion,
            Authentication authentication) {

        UserContext context = resolverContexto(authentication);

        BarberAgendaRequest request = new BarberAgendaRequest();
        request.setVista(vista);
        request.setFecha(fecha);
        request.setNavegacion(navegacion);

        return ResponseEntity.ok(barberAgendaUseCase.obtenerAgendaFutura(request, context));
    }

    @GetMapping("/agenda/export")
    public ResponseEntity<byte[]> exportarPdf(
            @RequestParam @NotNull ViewType vista,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) Navegacion navegacion,
            Authentication authentication) {

        UserContext context = resolverContexto(authentication);

        BarberAgendaRequest request = new BarberAgendaRequest();
        request.setVista(vista);
        request.setFecha(fecha);
        request.setNavegacion(navegacion);

        byte[] pdf = barberAgendaUseCase.exportarAgendaPdf(request, context);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "agenda.pdf");

        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    

    private UserContext resolverContexto(Authentication authentication) {
        String email = authentication.getName();

        Employee employee = employeeRepository.findByUserEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Empleado no encontrado para: " + email));

        return UserContext.builder()
                .userId(employee.getId())
                .role(employee.getUser().getRole())
                .build();
    }
}