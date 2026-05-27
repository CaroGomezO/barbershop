package com.example.barbershop.application.usecase;

import org.springframework.transaction.annotation.Transactional;

import com.example.barbershop.application.dto.DisableServiceResponse;
import com.example.barbershop.application.port.in.DisableServiceUseCase;
import com.example.barbershop.application.port.out.ServiceRepositoryPort;
import com.example.barbershop.domain.exception.ServiceAlreadyDisabledException;
import com.example.barbershop.domain.exception.ServiceNotFoundException;
import com.example.barbershop.domain.model.Service;

import lombok.RequiredArgsConstructor;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class DisableServiceUseCaseImpl implements DisableServiceUseCase {
    private final ServiceRepositoryPort serviceRepository;

    @Override
    @Transactional
    public DisableServiceResponse disable(Long serviceId) {

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ServiceNotFoundException(serviceId));

        if (!service.isActive()) {
            throw new ServiceAlreadyDisabledException();
        }

        long activeAppointments = serviceRepository
                .countActiveAppointmentsByServiceId(serviceId);

        service.setActive(false);
        Service updated = serviceRepository.update(service);

        String message = activeAppointments > 0
                ? String.format(
                    "El servicio '%s' fue deshabilitado. Existen %d cita(s) activa(s) " +
                    "asociadas que se mantendrán hasta su ejecución o cancelación normal.",
                    updated.getName(), activeAppointments)
                : String.format(
                    "El servicio '%s' fue deshabilitado correctamente.",
                    updated.getName());

        return DisableServiceResponse.builder()
                .serviceId(updated.getId())
                .name(updated.getName())
                .isActive(updated.isActive())
                .activeAppointmentsCount(activeAppointments)
                .message(message)
                .build();
    }
}
