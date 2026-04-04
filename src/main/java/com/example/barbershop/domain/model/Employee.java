package com.example.barbershop.domain.model;

import java.util.Set;

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
public class Employee {
    private Long id;
    private User user;
    private String documentNumber;
    private String names;
    private String lastNames;
    private String phoneNumber;
    private String address;
    private boolean isActive;
    private Set<Service> services;
}
