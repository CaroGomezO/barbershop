package com.example.barbershop.application.port.in;

import java.util.List;

import com.example.barbershop.application.dto.AvailabilityRequest;
import com.example.barbershop.application.dto.AvailabilityResponse;

public interface ScheduleAvailabilityUseCase {
    List<AvailabilityResponse> schedule(String email, AvailabilityRequest request);
}
