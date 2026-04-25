package com.example.barbershop.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cancellation {
    private Long id;
    private User user;
    private Appointment appointment;
    private LocalDateTime cancellationDate;
    private String reason;
    private Role role;
}
