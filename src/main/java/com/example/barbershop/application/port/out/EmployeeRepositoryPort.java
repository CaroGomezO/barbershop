package com.example.barbershop.application.port.out;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.barbershop.domain.model.Employee;

public interface EmployeeRepositoryPort {
    Employee save(Employee employee);
    Optional<Employee> findByUserEmail(String email);
    boolean existsByDocumentNumber(String documentNumber);
    List<Employee> findActiveEmployeesByServiceWithAvailability(Long serviceId, LocalDate from, LocalDate to);
}
