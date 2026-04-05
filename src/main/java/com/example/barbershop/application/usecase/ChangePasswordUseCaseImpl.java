package com.example.barbershop.application.usecase;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.barbershop.application.dto.ChangePasswordRequest;
import com.example.barbershop.application.port.in.ChangePasswordUseCase;
import com.example.barbershop.application.port.out.UserRepositoryPort;
import com.example.barbershop.domain.exception.InvalidCredentialsException;
import com.example.barbershop.domain.exception.PasswordMismatchException;
import com.example.barbershop.domain.exception.SamePasswordException;
import com.example.barbershop.domain.model.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChangePasswordUseCaseImpl implements ChangePasswordUseCase {
    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getHashPassword())) {
            throw new InvalidCredentialsException();
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getHashPassword())) {
            throw new SamePasswordException();
        }

        user.setHashPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordTemporary(false);
        userRepository.save(user);
    }
}
