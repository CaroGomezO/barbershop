package com.example.barbershop.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.barbershop.application.dto.AppointmentSummaryResponse;
import com.example.barbershop.application.dto.AvailableDatesResponse;
import com.example.barbershop.application.dto.ConfirmAppointmentRequest;
import com.example.barbershop.application.dto.ConfirmAppointmentResponse;
import com.example.barbershop.application.dto.EmployeeAvailabilityResponse;
import com.example.barbershop.application.dto.ServiceAvailabilityResponse;
import com.example.barbershop.application.dto.SlotResponse;
import com.example.barbershop.application.port.in.AppointmentUseCase;
import com.example.barbershop.application.port.out.AppointmentRepositoryPort;
import com.example.barbershop.application.port.out.AvailabilityRepositoryPort;
import com.example.barbershop.application.port.out.ClientRepositoryPort;
import com.example.barbershop.application.port.out.EmployeeRepositoryPort;
import com.example.barbershop.application.port.out.ServiceRepositoryPort;
import com.example.barbershop.domain.exception.EmployeeNotFoundException;
import com.example.barbershop.domain.exception.ServiceNotFoundException;
import com.example.barbershop.domain.exception.ServiceNotAvailableException;
import com.example.barbershop.domain.exception.SlotNotAvailableException;
import com.example.barbershop.domain.model.Appointment;
import com.example.barbershop.domain.model.AppointmentDetail;
import com.example.barbershop.domain.model.AppointmentStatus;
import com.example.barbershop.domain.model.Availability;
import com.example.barbershop.domain.model.Client;
import com.example.barbershop.domain.model.Employee;
import com.example.barbershop.domain.model.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class AppointmentUseCaseImpl implements AppointmentUseCase {

    private static final int MAX_DAYS = 15;

    private final ServiceRepositoryPort serviceRepository;
    private final EmployeeRepositoryPort employeeRepository;
    private final AvailabilityRepositoryPort availabilityRepository;
    private final AppointmentRepositoryPort appointmentRepository;
    private final ClientRepositoryPort clientRepository;

    @Override
    public List<ServiceAvailabilityResponse> getServicesWithAvailability() {
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusDays(MAX_DAYS);

        return serviceRepository.findAll().stream()
                .map(s -> ServiceAvailabilityResponse.builder()
                        .id(s.getId())
                        .name(s.getName())
                        .description(s.getDescription())
                        .price(s.getPrice())
                        .durationMinutes(s.getDurationMinutes())
                        .available(!availabilityRepository
                                .findAvailableDatesByService(s.getId(), from, to)
                                .isEmpty())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeAvailabilityResponse> getAvailableEmployees(Long serviceId) {
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusDays(MAX_DAYS);

        List<Employee> employees = employeeRepository
                .findActiveEmployeesByServiceWithAvailability(serviceId, from, to);

        if (employees.isEmpty()) throw new ServiceNotAvailableException();

        return employees.stream()
                .map(e -> EmployeeAvailabilityResponse.builder()
                        .id(e.getId())
                        .names(e.getNames())
                        .lastNames(e.getLastNames())
                        .services(e.getServices().stream()
                                .map(service -> service.getName())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public AvailableDatesResponse getAvailableDates(Long employeeId) {
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusDays(MAX_DAYS);

        List<LocalDate> available = availabilityRepository
                .findAvailableDatesByEmployee(employeeId, from, to);

        List<LocalDate> disabled = from.datesUntil(to.plusDays(1))
                .filter(d -> !available.contains(d))
                .collect(Collectors.toList());

        return AvailableDatesResponse.builder()
                .availableDates(available)
                .disabledDates(disabled)
                .build();
    }

    @Override
    public List<SlotResponse> getAvailableSlots(Long employeeId, LocalDate date, List<Long> serviceIds) {

        List<Service> services = serviceRepository.findAllById(serviceIds);

        int totalDuration = services.stream()
                .mapToInt(s -> s.getDurationMinutes())
                .sum();

        List<Availability> ranges = availabilityRepository.findByEmployeeIdAndDate(employeeId, date);

        List<SlotResponse> slots = new ArrayList<>();

        for (Availability range : ranges) {
            LocalTime current = range.getStartTime();

            while (!current.plusMinutes(totalDuration).isAfter(range.getEndTime())) {
                LocalTime end = current.plusMinutes(totalDuration);

                boolean overlaps = appointmentRepository.existsConfirmedOverlap(employeeId, date, current, end);

                if (!overlaps) {
                    slots.add(SlotResponse.builder()
                            .startTime(current)
                            .endTime(end)
                            .build());
                }

                int minDuration = services.stream()
                        .mapToInt(s -> s.getDurationMinutes())
                        .min().orElse(15);
                current = current.plusMinutes(minDuration);
            }
        }

        return slots;
    }

    @Override
    public AppointmentSummaryResponse getSummary(Long employeeId, LocalDate date, LocalTime startTime, List<Long> serviceIds) {

        Employee employee = employeeRepository.findActiveEmployeesByServiceWithAvailability(
                serviceIds.get(0),
                date.minusDays(1),
                date.plusDays(1)
        ).stream()
                .filter(e -> e.getId().equals(employeeId))
                .findFirst()
                .orElseThrow(EmployeeNotFoundException::new);

        List<Service> services = serviceRepository.findAllById(serviceIds);

        BigDecimal totalPrice = services.stream()
                .map(s -> s.getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalDuration = services.stream()
                .mapToInt(s -> s.getDurationMinutes())
                .sum();

        return AppointmentSummaryResponse.builder()
                .employeeNames(employee.getNames())
                .employeeLastNames(employee.getLastNames())
                .date(date)
                .startTime(startTime)
                .endTime(startTime.plusMinutes(totalDuration))
                .serviceNames(services.stream()
                        .map(s -> s.getName())
                        .collect(Collectors.toList()))
                .totalPrice(totalPrice)
                .totalDuration(totalDuration)
                .build();
    }

    @Override
    @Transactional
    public ConfirmAppointmentResponse confirm(String clientEmail, ConfirmAppointmentRequest request) {

        List<Service> services = serviceRepository.findAllById(request.getServiceIds());

        if (services.size() != request.getServiceIds().size()) {
            throw new ServiceNotFoundException(0L);
        }

        int totalDuration = services.stream()
                .mapToInt(s -> s.getDurationMinutes())
                .sum();

        LocalTime endTime = request.getStartTime().plusMinutes(totalDuration);

        boolean overlaps = appointmentRepository.existsConfirmedOverlap(
                request.getEmployeeId(), request.getDate(),
                request.getStartTime(), endTime);

        if (overlaps) throw new SlotNotAvailableException();

        Client client = clientRepository.findByUserEmail(clientEmail)
                .orElseThrow(() -> new IllegalStateException("Cliente no encontrado"));

        Employee employee = employeeRepository.findActiveEmployeesByServiceWithAvailability(
                request.getServiceIds().get(0),
                request.getDate().minusDays(1),
                request.getDate().plusDays(1)
        ).stream()
                .filter(e -> e.getId().equals(request.getEmployeeId()))
                .findFirst()
                .orElseThrow(EmployeeNotFoundException::new);

        BigDecimal totalPrice = services.stream()
                .map(s -> s.getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Set<AppointmentDetail> appointmentServices = services.stream()
        .map(s -> {
            AppointmentDetail detail = AppointmentDetail.builder()
                    .service(s)
                    .price(s.getPrice())
                    .durationMinutes(s.getDurationMinutes())
                    .build();
            return detail;
        })
        .collect(Collectors.toSet());


        Appointment appointment = Appointment.builder()
                .client(client)
                .employee(employee)
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(endTime)
                .status(AppointmentStatus.CONFIRMADA)
                .details(appointmentServices)
                .build();

        Appointment saved = appointmentRepository.save(appointment);

        return ConfirmAppointmentResponse.builder()
                .appointmentId(saved.getId())
                .employeeNames(employee.getNames())
                .employeeLastNames(employee.getLastNames())
                .date(saved.getDate())
                .startTime(saved.getStartTime())
                .endTime(saved.getEndTime())
                .services(services.stream()
                        .map(s -> s.getName())
                        .collect(Collectors.toList()))
                .totalPrice(totalPrice)
                .totalDuration(totalDuration)
                .status(saved.getStatus().name())
                .build();
    }
}
