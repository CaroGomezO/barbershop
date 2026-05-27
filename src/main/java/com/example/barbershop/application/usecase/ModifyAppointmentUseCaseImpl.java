package com.example.barbershop.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.barbershop.application.dto.ModifyAppointmentRequest;
import com.example.barbershop.application.dto.ModifyAppointmentResponse;
import com.example.barbershop.application.port.in.ModifyAppointmentUseCase;
import com.example.barbershop.application.port.out.AppointmentRepositoryPort;
import com.example.barbershop.application.port.out.AvailabilityRepositoryPort;
import com.example.barbershop.application.port.out.EmployeeRepositoryPort;
import com.example.barbershop.application.port.out.ServiceRepositoryPort;
import com.example.barbershop.domain.exception.AppointmentNotFoundException;
import com.example.barbershop.domain.exception.AppointmentNotModifiableException;
import com.example.barbershop.domain.exception.EmployeeNotFoundException;
import com.example.barbershop.domain.exception.ServiceNotFoundException;
import com.example.barbershop.domain.exception.ServiceNotAssignedToEmployeeException;
import com.example.barbershop.domain.exception.SlotNotAvailableException;
import com.example.barbershop.domain.exception.UnauthorizedAccessException;
import com.example.barbershop.domain.model.Appointment;
import com.example.barbershop.domain.model.AppointmentDetail;
import com.example.barbershop.domain.model.AppointmentStatus;
import com.example.barbershop.domain.model.Availability;
import com.example.barbershop.domain.model.Employee;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ModifyAppointmentUseCaseImpl implements ModifyAppointmentUseCase {
    private final AppointmentRepositoryPort appointmentRepository;
    private final EmployeeRepositoryPort employeeRepository;
    private final ServiceRepositoryPort serviceRepository;
    private final AvailabilityRepositoryPort availabilityRepository;

    @Override
    @Transactional
    public ModifyAppointmentResponse modify(Long appointmentId, String clientEmail, ModifyAppointmentRequest request) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(AppointmentNotFoundException::new);

        if (!appointment.getClient().getUser().getEmail().equals(clientEmail)) {
            throw new UnauthorizedAccessException();
        }

        if (appointment.getStatus() != AppointmentStatus.CONFIRMADA) {
            throw new AppointmentNotModifiableException();
        }

        Employee employee = resolveEmployee(request, appointment);

        boolean servicesChanged = request.getServiceIds() != null
        && !request.getServiceIds().isEmpty();

        List<com.example.barbershop.domain.model.Service> services = resolveServices(request, appointment);

        boolean employeeChanged = request.getEmployeeId() != null
        && !request.getEmployeeId().equals(appointment.getEmployee().getId());

        if (servicesChanged || employeeChanged) {
            for (com.example.barbershop.domain.model.Service service : services) {
                if (!employeeRepository.hasService(employee.getId(), service.getId())) {
                    throw new ServiceNotAssignedToEmployeeException(service.getName());
                }
            }
        }

        LocalDate date = request.getDate() != null
                ? request.getDate()
                : appointment.getDate();

        LocalTime startTime = request.getStartTime() != null
                ? request.getStartTime()
                : appointment.getStartTime();

        int totalDuration = services.stream()
                .mapToInt(com.example.barbershop.domain.model.Service::getDurationMinutes)
                .sum();

        LocalTime endTime = startTime.plusMinutes(totalDuration);

        boolean dateOrTimeChanged = request.getDate() != null || request.getStartTime() != null || employeeChanged;
        if (dateOrTimeChanged) {
            List<Availability> availabilities = availabilityRepository
                    .findByEmployeeIdAndDate(employee.getId(), date);
 
            boolean withinAvailability = availabilities.stream().anyMatch(a ->
                    !startTime.isBefore(a.getStartTime())
                    && startTime.isBefore(a.getEndTime())
                    && !endTime.isAfter(a.getEndTime())
            );
 
            if (!withinAvailability) {
                throw new SlotNotAvailableException();
            }
        }

        boolean overlaps = appointmentRepository.existsConfirmedOverlapExcluding(
                employee.getId(), date, startTime, endTime, appointmentId);

        if (overlaps) {
            throw new SlotNotAvailableException();
        }

        BigDecimal totalPrice = services.stream()
                .map(com.example.barbershop.domain.model.Service::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Set<AppointmentDetail> details = services.stream()
                .map(s -> AppointmentDetail.builder()
                        .service(s)
                        .price(s.getPrice())
                        .durationMinutes(s.getDurationMinutes())
                        .build())
                .collect(Collectors.toSet());

        appointment.setEmployee(employee);
        appointment.setDate(date);
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setTotalPrice(totalPrice);
        appointment.setDetails(details);
        appointment.setStatus(AppointmentStatus.MODIFICADA);

        Appointment updated = appointmentRepository.update(appointment);

        return ModifyAppointmentResponse.builder()
                .appointmentId(updated.getId())
                .employeeNames(employee.getNames())
                .employeeLastNames(employee.getLastNames())
                .date(updated.getDate())
                .startTime(updated.getStartTime())
                .endTime(updated.getEndTime())
                .services(services.stream()
                        .map(com.example.barbershop.domain.model.Service::getName)
                        .collect(Collectors.toList()))
                .totalPrice(totalPrice)
                .totalDuration(totalDuration)
                .status(updated.getStatus().name())
                .message("Tu cita ha sido modificada exitosamente.")
                .build();
    }

    private Employee resolveEmployee(ModifyAppointmentRequest request, Appointment appointment) {
        if (request.getEmployeeId() != null) {
            return employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(EmployeeNotFoundException::new);
        }
        return appointment.getEmployee();
    }

    private List<com.example.barbershop.domain.model.Service> resolveServices(
            ModifyAppointmentRequest request, Appointment appointment) {
        if (request.getServiceIds() != null && !request.getServiceIds().isEmpty()) {
            List<com.example.barbershop.domain.model.Service> services =
                    serviceRepository.findAllById(request.getServiceIds());

            if (services.size() != request.getServiceIds().size()) {
                throw new ServiceNotFoundException(0L);
            }

            services.forEach(s -> {
                if (!s.isActive()) {
                    throw new ServiceNotFoundException(s.getId());
                }
            });
            
            return services;
        }

        return appointment.getDetails().stream()
                .map(AppointmentDetail::getService)
                .collect(Collectors.toList());
    }
}
