package com.example.barbershop.application.usecase;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import com.example.barbershop.application.dto.UpdateEmployeeRequest;
import com.example.barbershop.application.dto.UpdateEmployeeResponse;
import com.example.barbershop.application.port.in.UpdateEmployeeUseCase;
import com.example.barbershop.application.port.out.EmployeeRepositoryPort;
import com.example.barbershop.application.port.out.ServiceRepositoryPort;
import com.example.barbershop.domain.exception.EmployeeNotFoundException;
import com.example.barbershop.domain.exception.ServiceNotFoundException;
import com.example.barbershop.domain.model.Employee;
import com.example.barbershop.domain.model.Service;

import lombok.RequiredArgsConstructor;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class UpdateEmployeeUseCaseImpl implements UpdateEmployeeUseCase {
    private final EmployeeRepositoryPort employeeRepository;
    private final ServiceRepositoryPort serviceRepository;

    @Override
    @Transactional
    public UpdateEmployeeResponse update(Long employeeId, UpdateEmployeeRequest request) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(EmployeeNotFoundException::new);

        if (request.getNames() != null && !request.getNames().isBlank()) {
            employee.setNames(request.getNames());
        }
        if (request.getLastNames() != null && !request.getLastNames().isBlank()) {
            employee.setLastNames(request.getLastNames());
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            employee.setPhoneNumber(request.getPhone());
        }
        if (request.getAddress() != null && !request.getAddress().isBlank()) {
            employee.setAddress(request.getAddress());
        }
        if (request.getServiceIds() != null && !request.getServiceIds().isEmpty()) {
            List<Service> services = serviceRepository
                    .findAllById(request.getServiceIds());

            if (services.size() != request.getServiceIds().size()) {
                throw new ServiceNotFoundException(0L);
            }
            employee.setServices(new HashSet<>(services));
        }

        Employee updated = employeeRepository.update(employee);

        return UpdateEmployeeResponse.builder()
                .employeeId(updated.getId())
                .names(updated.getNames())
                .lastNames(updated.getLastNames())
                .phone(updated.getPhoneNumber())
                .address(updated.getAddress())
                .isActive(updated.isActive())
                .services(updated.getServices().stream()
                        .map(Service::getName)
                        .collect(Collectors.toList()))
                .message("Los datos del barbero han sido actualizados correctamente.")
                .build();
    }
}
