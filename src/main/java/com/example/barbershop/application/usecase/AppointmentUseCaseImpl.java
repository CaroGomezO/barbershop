package com.example.barbershop.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;

import com.example.barbershop.application.dto.AppointmentSummaryResponse;
import com.example.barbershop.application.dto.AvailableDatesResponse;
import com.example.barbershop.application.dto.CancelAppointmentRequest;
import com.example.barbershop.application.dto.CancelAppointmentResponse;
import com.example.barbershop.application.dto.ConfirmAppointmentRequest;
import com.example.barbershop.application.dto.ConfirmAppointmentResponse;
import com.example.barbershop.application.dto.EmployeeAvailabilityResponse;
import com.example.barbershop.application.dto.ServiceAvailabilityResponse;
import com.example.barbershop.application.dto.SlotResponse;
import com.example.barbershop.application.port.in.AppointmentUseCase;
import com.example.barbershop.application.port.out.AppointmentRepositoryPort;
import com.example.barbershop.application.port.out.AvailabilityRepositoryPort;
import com.example.barbershop.application.port.out.CancellationRepositoryPort;
import com.example.barbershop.application.port.out.ClientRepositoryPort;
import com.example.barbershop.application.port.out.EmployeeRepositoryPort;
import com.example.barbershop.application.port.out.ServiceRepositoryPort;
import com.example.barbershop.application.security.UserContext;
import com.example.barbershop.domain.exception.AppointmentAlreadyCancelledException;
import com.example.barbershop.domain.exception.AppointmentNotFoundException;
import com.example.barbershop.domain.exception.AppointmentNotOwnedByUserException;
import com.example.barbershop.domain.exception.BarberCancellationReasonNotProvidedException;
import com.example.barbershop.domain.exception.CancellationLimitExceededException;
import com.example.barbershop.domain.exception.EmployeeNotFoundException;
import com.example.barbershop.domain.exception.ServiceNotFoundException;
import com.example.barbershop.domain.exception.ServiceNotAvailableException;
import com.example.barbershop.domain.exception.SlotNotAvailableException;
import com.example.barbershop.domain.model.Appointment;
import com.example.barbershop.domain.model.AppointmentDetail;
import com.example.barbershop.domain.model.AppointmentStatus;
import com.example.barbershop.domain.model.Availability;
import com.example.barbershop.domain.model.Cancellation;
import com.example.barbershop.domain.model.Client;
import com.example.barbershop.domain.model.Employee;
import com.example.barbershop.domain.model.Role;
import com.example.barbershop.domain.model.Service;
import com.example.barbershop.domain.model.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class AppointmentUseCaseImpl implements AppointmentUseCase {

        private static final int MAX_DAYS = 15;
        private static final int PERMITED_CANCELLATIONS_PER_MONTH = 3;

        private final ServiceRepositoryPort serviceRepository;
        private final EmployeeRepositoryPort employeeRepository;
        private final AvailabilityRepositoryPort availabilityRepository;
        private final AppointmentRepositoryPort appointmentRepository;
        private final ClientRepositoryPort clientRepository;
        private final CancellationRepositoryPort cancellationRepository;

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

                if (employees.isEmpty())
                        throw new ServiceNotAvailableException();

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

                                boolean overlaps = appointmentRepository.existsConfirmedOverlap(employeeId, date,
                                                current, end);

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
        public AppointmentSummaryResponse getSummary(Long employeeId, LocalDate date, LocalTime startTime,
                        List<Long> serviceIds) {

                Employee employee = employeeRepository.findActiveEmployeesByServiceWithAvailability(
                                serviceIds.get(0),
                                date.minusDays(1),
                                date.plusDays(1)).stream()
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

                if (overlaps)
                        throw new SlotNotAvailableException();

                Client client = clientRepository.findByUserEmail(clientEmail)
                                .orElseThrow(() -> new IllegalStateException("Cliente no encontrado"));

                Employee employee = employeeRepository.findActiveEmployeesByServiceWithAvailability(
                                request.getServiceIds().get(0),
                                request.getDate().minusDays(1),
                                request.getDate().plusDays(1)).stream()
                                .filter(e -> e.getId().equals(request.getEmployeeId()))
                                .findFirst()
                                .orElseThrow(EmployeeNotFoundException::new);

                BigDecimal totalPrice = services.stream()
                                .map(s -> s.getPrice())
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                Set<AppointmentDetail> details = services.stream()
                                .map(s -> AppointmentDetail.builder()
                                                .service(s)
                                                .price(s.getPrice())
                                                .durationMinutes(s.getDurationMinutes())
                                                .build())
                                .collect(Collectors.toSet());

                Appointment appointment = Appointment.builder()
                                .client(client)
                                .employee(employee)
                                .date(request.getDate())
                                .startTime(request.getStartTime())
                                .endTime(endTime)
                                .status(AppointmentStatus.CONFIRMADA)
                                .totalPrice(totalPrice)
                                .details(details)
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

        @Transactional
        @Override
        public CancelAppointmentResponse cancel(CancelAppointmentRequest request, UserContext context) {

                LocalDateTime now = LocalDateTime.now();
                LocalDateTime from = now.minusDays(30);

                validateCancellationLimit(context, from, now);

                Appointment appointment = appointmentRepository
                                .findById(request.getAppointmentId())
                                .orElseThrow(() -> new AppointmentNotFoundException(request.getAppointmentId()));

                validateNotAlreadyCancelled(appointment);

                if (context.getRole() == Role.CLIENTE) {
                        validateClient(context, appointment);
                } else if (context.getRole() == Role.BARBERO) {
                        validateBarber(context, appointment, request);
                } else {
                        throw new AccessDeniedException("No autorizado para acceder a la funcionalidad");
                }

                appointment.setStatus(AppointmentStatus.CANCELADA);

                Cancellation cancellation = saveCancellation(context, appointment, request);

                return buildResponse(cancellation, request, context);
        }

        private void validateCancellationLimit(UserContext context, LocalDateTime from, LocalDateTime to) {
                Long cancellations = cancellationRepository
                                .countByUserIdAndCancellationDateBetween(context.getUserId(), from, to);

                if (cancellations > (long) PERMITED_CANCELLATIONS_PER_MONTH) {
                        throw new CancellationLimitExceededException();
                }
        }

        private void validateNotAlreadyCancelled(Appointment appointment) {
                if (appointment.getStatus() == AppointmentStatus.CANCELADA) {
                        throw new AppointmentAlreadyCancelledException(appointment.getId());
                }
        }

        private void validateClient(UserContext context, Appointment appointment) {
                if (!appointment.getClient().getUser().getId().equals(context.getUserId())) {
                        throw new AppointmentNotOwnedByUserException(
                                        appointment.getId(), context.getUserId());
                }
        }

        private void validateBarber(UserContext context, Appointment appointment, CancelAppointmentRequest request) {

                if (request.getReason() == null || request.getReason().isBlank()) {
                        throw new BarberCancellationReasonNotProvidedException();
                }

                if (!appointment.getEmployee().getUser().getId().equals(context.getUserId())) {
                        throw new AppointmentNotOwnedByUserException(
                                        appointment.getId(), context.getUserId());
                }
        }

        private Cancellation saveCancellation(UserContext context, Appointment appointment, CancelAppointmentRequest request) {

                return cancellationRepository.save(
                                Cancellation.builder()
                                                .user(getUserFromAppointment(context, appointment))
                                                .appointment(appointment)
                                                .cancellationDate(LocalDateTime.now())
                                                .role(context.getRole())
                                                .build());
        }
        private User getUserFromAppointment(UserContext context, Appointment appointment) {
                if (context.getRole() == Role.CLIENTE) {
                        return appointment.getClient().getUser();
                }
                return appointment.getEmployee().getUser();
        }

        private CancelAppointmentResponse buildResponse(
                        Cancellation cancellation,
                        CancelAppointmentRequest request,
                        UserContext context) {

                return CancelAppointmentResponse.builder()
                                .id(cancellation.getId())
                                .cancellationDate(cancellation.getCancellationDate())
                                .status(cancellation.getAppointment().getStatus())
                                .reason(context.getRole() == Role.CLIENTE ? null : request.getReason())
                                .cancelledBy(context.getRole())
                                .build();
        }

}
