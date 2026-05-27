package com.example.barbershop.application.usecase;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.barbershop.application.dto.CreateServiceRequest;
import com.example.barbershop.application.dto.ServiceResponse;
import com.example.barbershop.application.port.in.ManageServicesUseCase;
import com.example.barbershop.application.port.out.ServiceRepositoryPort;
import com.example.barbershop.domain.exception.DuplicateResourceException;
import com.example.barbershop.domain.exception.ResourceNotFoundException;

@Service
@Transactional
public class ManageServicesUseCaseImpl implements ManageServicesUseCase {

    private final ServiceRepositoryPort serviceRepository;
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

    public ManageServicesUseCaseImpl(ServiceRepositoryPort serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public ServiceResponse createService(CreateServiceRequest request) {
        validateRequest(request);

        if (serviceRepository.existsByName(request.getName().trim())) {
            throw new DuplicateResourceException("Ya existe un servicio con el nombre: " + request.getName());
        }

        com.example.barbershop.domain.model.Service newService = new com.example.barbershop.domain.model.Service();
        newService.setName(request.getName());
        newService.setDescription(request.getDescription());
        newService.setPrice(request.getPrice());
        newService.setDurationMinutes(request.getDurationMinutes());
        newService.setActive(true);

        com.example.barbershop.domain.model.Service saved = serviceRepository.save(newService);

        return toResponse(saved);
    }

    @Override
    public ServiceResponse updateService(Long id, CreateServiceRequest request) {
        validateRequest(request);

        com.example.barbershop.domain.model.Service existing = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio", id));

        if (!existing.getName().equalsIgnoreCase(request.getName().trim()) &&
                serviceRepository.existsByName(request.getName().trim())) {
            throw new DuplicateResourceException("Ya existe un servicio con el nombre: " + request.getName());
        }

        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setPrice(request.getPrice());
        existing.setDurationMinutes(request.getDurationMinutes());

        com.example.barbershop.domain.model.Service updated = serviceRepository.save(existing);

        return toResponse(updated);
    }

    @Override
    public void deleteService(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Servicio", id);
        }
        serviceRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceResponse> getAllServices() {
        return serviceRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponse getServiceById(Long id) {
        com.example.barbershop.domain.model.Service serviceDomain = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio", id));
        return toResponse(serviceDomain);
    }

    private ServiceResponse toResponse(com.example.barbershop.domain.model.Service serviceDomain) {
        ServiceResponse response = new ServiceResponse();
        response.setId(serviceDomain.getId());
        response.setName(serviceDomain.getName());
        response.setDescription(serviceDomain.getDescription());
        response.setPrice(formatPrice(serviceDomain.getPrice()));
        response.setDurationMinutes(serviceDomain.getDurationMinutes());
        response.setActive(serviceDomain.isActive());
        return response;
    }

    private String formatPrice(java.math.BigDecimal price) {
        if (price == null) return "$0";
        return CURRENCY_FORMATTER.format(price);
    }

    private void validateRequest(CreateServiceRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Los datos del servicio no pueden ser nulos");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre del servicio es obligatorio");
        }
        if (request.getPrice() == null || request.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero");
        }
        if (request.getDurationMinutes() == null || request.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException("La duración debe ser mayor a cero");
        }
    }
}