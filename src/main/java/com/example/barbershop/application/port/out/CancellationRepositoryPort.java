package com.example.barbershop.application.port.out;

import java.time.LocalDateTime;

import com.example.barbershop.domain.model.Cancellation;

public interface CancellationRepositoryPort {
    Cancellation save(Cancellation cancellation);
    Long countByUserIdAndCancellationDateBetween(Long userId, LocalDateTime from, LocalDateTime to);
}
