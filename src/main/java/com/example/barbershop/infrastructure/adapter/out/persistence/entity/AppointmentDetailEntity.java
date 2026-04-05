package com.example.barbershop.infrastructure.adapter.out.persistence.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "appointment_services")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private AppointmentEntity appointment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    @Column(name = "price_charged", nullable = false)
    private BigDecimal price;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;
}
