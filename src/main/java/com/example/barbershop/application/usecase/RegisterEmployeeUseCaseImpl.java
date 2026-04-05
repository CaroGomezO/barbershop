package com.example.barbershop.application.usecase;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.barbershop.application.dto.RegisterEmployeeRequest;
import com.example.barbershop.application.dto.RegisterEmployeeResponse;
import com.example.barbershop.application.port.in.RegisterEmployeeUseCase;
import com.example.barbershop.application.port.out.EmployeeRepositoryPort;
import com.example.barbershop.application.port.out.ServiceRepositoryPort;
import com.example.barbershop.application.port.out.UserRepositoryPort;
import com.example.barbershop.domain.exception.DocumentAlreadyExistsException;
import com.example.barbershop.domain.exception.EmailAlreadyExistsException;
import com.example.barbershop.domain.exception.ServiceNotFoundException;
import com.example.barbershop.domain.model.Employee;
import com.example.barbershop.domain.model.Role;
import com.example.barbershop.domain.model.Service;
import com.example.barbershop.domain.model.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class RegisterEmployeeUseCaseImpl implements RegisterEmployeeUseCase {
    private final UserRepositoryPort userRepository;
    private final EmployeeRepositoryPort employeeRepository;
    private final ServiceRepositoryPort serviceRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public RegisterEmployeeResponse register(RegisterEmployeeRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }
        if (employeeRepository.existsByDocumentNumber(request.getDocumentNumber())) {
            throw new DocumentAlreadyExistsException(request.getDocumentNumber());
        }

        List<Service> services =
                serviceRepository.findAllById(request.getServiceIds());

        if (services.size() != request.getServiceIds().size()) {
            throw new ServiceNotFoundException(0L);
        }

        User user = User.builder()
                .email(request.getEmail())
                .hashPassword(passwordEncoder.encode(request.getPassword()))
                .role(Role.BARBERO)
                .isPasswordTemporary(true)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        Employee employee = Employee.builder()
                .user(savedUser)
                .documentNumber(request.getDocumentNumber())
                .names(request.getNames())
                .lastNames(request.getLastNames())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .isActive(true)
                .services(new HashSet<>(services))
                .build();

        Employee saved = employeeRepository.save(employee);

        return RegisterEmployeeResponse.builder()
                .employeeId(saved.getId())
                .names(saved.getNames())
                .lastNames(saved.getLastNames())
                .email(savedUser.getEmail())
                .documentNumber(saved.getDocumentNumber())
                .address(saved.getAddress())
                .isActive(saved.isActive())
                .services(services.stream()
                        .map(Service::getName)
                        .collect(Collectors.toList()))
                .build();
    }
}
