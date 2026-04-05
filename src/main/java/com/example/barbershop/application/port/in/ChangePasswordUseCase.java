package com.example.barbershop.application.port.in;

import com.example.barbershop.application.dto.ChangePasswordRequest;

public interface ChangePasswordUseCase {
    void changePassword(String email, ChangePasswordRequest request);
}
