package com.example.barbershop.application.usecase;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.barbershop.application.dto.RegisterClientRequest;
import com.example.barbershop.application.dto.RegisterClientResponse;
import com.example.barbershop.application.port.in.RegisterClientUseCase;
import com.example.barbershop.application.port.out.ClientRepositoryPort;
import com.example.barbershop.application.port.out.UserRepositoryPort;
import com.example.barbershop.domain.exception.EmailAlreadyExistsException;
import com.example.barbershop.domain.exception.PasswordMismatchException;
import com.example.barbershop.domain.model.Client;
import com.example.barbershop.domain.model.Role;
import com.example.barbershop.domain.model.User;
import com.example.barbershop.infrastructure.security.JwtService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisterClientUseCaseImpl implements RegisterClientUseCase {
    private final UserRepositoryPort userRepository;
    private final ClientRepositoryPort clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public RegisterClientResponse register(RegisterClientRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .hashPassword(passwordEncoder.encode(request.getPassword()))
                .role(Role.CLIENTE)
                .isPasswordTemporary(false)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        Client client = Client.builder()
                .user(savedUser)
                .names(request.getNames())
                .lastNames(request.getLastNames())
                .phoneNumber(request.getPhoneNumber())
                .build();

        clientRepository.save(client);

        String token = jwtService.generateToken(savedUser);

        return RegisterClientResponse.builder()
                .token(token)
                .names(request.getNames())
                .message("¡Bienvenido, " + request.getNames()
                        + "! Tu cuenta fue creada exitosamente.")
                .build();
    }
}
