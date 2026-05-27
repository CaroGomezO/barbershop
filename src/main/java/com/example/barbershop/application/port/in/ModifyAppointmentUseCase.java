package com.example.barbershop.application.port.in;

import com.example.barbershop.application.dto.ModifyAppointmentRequest;
import com.example.barbershop.application.dto.ModifyAppointmentResponse;

public interface ModifyAppointmentUseCase {
    ModifyAppointmentResponse modify(Long appointmentId, String clientEmail, ModifyAppointmentRequest request);
}
