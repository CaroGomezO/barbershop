package com.example.barbershop.domain.model;

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
public class Client {
    private Long id;
    private User user;
    private String names;
    private String lastNames;
    private String phoneNumber;
}
