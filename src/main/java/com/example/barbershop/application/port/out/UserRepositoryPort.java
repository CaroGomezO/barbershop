package com.example.barbershop.application.port.out;

import java.util.Optional;

import com.example.barbershop.domain.model.User;

public interface UserRepositoryPort {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    User save(User user);
}
