package com.example.barbershop.application.port.in;

import java.util.List;

import com.example.barbershop.application.dto.CreateServiceRequest;
import com.example.barbershop.application.dto.ServiceResponse;

public interface ManageServicesUseCase {
    ServiceResponse createService(CreateServiceRequest request);
    ServiceResponse updateService(Long id, CreateServiceRequest request);
    void deleteService(Long id);
    List<ServiceResponse> getAllServices();
    ServiceResponse getServiceById(Long id);
}