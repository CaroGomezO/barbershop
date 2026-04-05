package com.example.barbershop.application.usecase;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.barbershop.application.dto.LoginRequest;
import com.example.barbershop.application.dto.LoginResponse;
import com.example.barbershop.application.port.in.LoginUseCase;
import com.example.barbershop.application.port.out.ClientRepositoryPort;
import com.example.barbershop.application.port.out.EmployeeRepositoryPort;
import com.example.barbershop.application.port.out.UserRepositoryPort;
import com.example.barbershop.domain.exception.InvalidCredentialsException;
import com.example.barbershop.domain.model.Role;
import com.example.barbershop.domain.model.User;
import com.example.barbershop.infrastructure.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginUseCaseImpl implements LoginUseCase {
    private final UserRepositoryPort userRepository;
    private final ClientRepositoryPort clientRepository;
    private final EmployeeRepositoryPort employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getHashPassword())) {
            throw new InvalidCredentialsException();
        }

        // Obtener nombre según rol
        String names = resolveName(user);

        String token = jwtService.generateToken(user);

        return LoginResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .names(names)
                .isPasswordTemporary(user.isPasswordTemporary())
                .build();
    }

    private String resolveName(User user) {
        if (user.getRole() == Role.CLIENTE) {
            return clientRepository.findByUserEmail(user.getEmail())
                    .map(c -> c.getNames())
                    .orElse(user.getEmail());
        }

        return employeeRepository.findByUserEmail(user.getEmail())
                .map(e -> e.getNames())
                .orElse(user.getEmail());
    }

}
