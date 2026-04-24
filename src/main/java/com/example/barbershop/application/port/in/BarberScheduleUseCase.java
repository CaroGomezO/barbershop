package com.example.barbershop.application.port.in;
import java.time.LocalDate;

import com.example.barbershop.application.dto.BarberScheduleResponse;

public interface BarberScheduleUseCase {
    BarberScheduleResponse getBarberSchedule (Long employeeId, LocalDate from, LocalDate to);

}
