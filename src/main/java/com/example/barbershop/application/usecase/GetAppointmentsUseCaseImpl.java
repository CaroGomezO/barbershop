package com.example.barbershop.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.barbershop.application.dto.AppointmentResponse;
import com.example.barbershop.application.port.in.GetAppointmentsUseCase;
import com.example.barbershop.application.port.out.AppointmentRepositoryPort;
import com.example.barbershop.domain.exception.ResourceNotFoundException;
import com.example.barbershop.domain.model.Appointment;
import com.example.barbershop.domain.model.AppointmentStatus;

@Service
@Transactional(readOnly = true)
public class GetAppointmentsUseCaseImpl implements GetAppointmentsUseCase {

    private final AppointmentRepositoryPort appointmentRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    public GetAppointmentsUseCaseImpl(AppointmentRepositoryPort appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }
        return appointmentRepository.findByDate(date).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("El estado no puede ser nulo o vacío");
        }

        AppointmentStatus appointmentStatus;
        try {
            appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado inválido: " + status);
        }

        return appointmentRepository.findByStatus(appointmentStatus).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la fecha final");
        }
        return appointmentRepository.findByDateBetween(startDate, endDate).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentResponse getAppointmentById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", id));
        return toResponse(appointment);
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByEmployee(Long employeeId) {
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("ID de empleado inválido");
        }
        return appointmentRepository.findByEmployeeId(employeeId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        AppointmentResponse response = new AppointmentResponse();
        response.setId(appointment.getId());

        if (appointment.getClient() != null) {
            response.setClientName(appointment.getClient().getNames() + " " + appointment.getClient().getLastNames());
        }

        if (appointment.getEmployee() != null) {
            response.setEmployeeName(appointment.getEmployee().getNames() + " " + appointment.getEmployee().getLastNames());
        }

        if (appointment.getDate() != null) {
            response.setDate(appointment.getDate().format(DATE_FORMATTER));
        }
        if (appointment.getStartTime() != null) {
            response.setStartTime(appointment.getStartTime().format(TIME_FORMATTER));
        }
        if (appointment.getEndTime() != null) {
            response.setEndTime(appointment.getEndTime().format(TIME_FORMATTER));
        }

        response.setStatus(appointment.getStatus() != null ? appointment.getStatus().name() : null);
        response.setTotalPrice(formatPrice(appointment.getTotalPrice()));

        if (appointment.getStartTime() != null && appointment.getEndTime() != null) {
            int duration = (int) java.time.Duration.between(appointment.getStartTime(), appointment.getEndTime()).toMinutes();
            response.setDurationMinutes(duration);
        }

        return response;
    }

    private String formatPrice(BigDecimal price) {
        if (price == null) return "$0";
        return String.format("$%,.0f", price);
    }
}