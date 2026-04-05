package com.example.barbershop.infrastructure.adapter.out.persistence.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.barbershop.application.port.out.UserRepositoryPort;
import com.example.barbershop.domain.model.Role;
import com.example.barbershop.domain.model.User;
import com.example.barbershop.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.example.barbershop.infrastructure.adapter.out.persistence.jpa.RoleJpaRepository;
import com.example.barbershop.infrastructure.adapter.out.persistence.jpa.UserJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {
    private final UserJpaRepository jpaRepository;
    private final RoleJpaRepository roleJpaRepository;

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        if (user.getId() != null) {
            UserEntity existing = jpaRepository.findById(user.getId())
                    .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
            existing.setHashPassword(user.getHashPassword());
            existing.setPasswordTemporary(user.isPasswordTemporary());
            return toDomain(jpaRepository.save(existing));
        }

        UserEntity entity = UserEntity.builder()
                .email(user.getEmail())
                .hashPassword(user.getHashPassword())
                .role(roleJpaRepository.findByName(user.getRole())
                        .orElseThrow(() -> new IllegalStateException(
                                "Rol no encontrado: " + user.getRole())))
                .isPasswordTemporary(user.isPasswordTemporary())
                .createdAt(user.getCreatedAt())
                .build();

        return toDomain(jpaRepository.save(entity));
    }

    public User toDomain(UserEntity e) {
        return User.builder()
                .id(e.getId())
                .email(e.getEmail())
                .hashPassword(e.getHashPassword())
                .role(Role.valueOf(e.getRole().getName().name()))
                .isPasswordTemporary(e.isPasswordTemporary())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
