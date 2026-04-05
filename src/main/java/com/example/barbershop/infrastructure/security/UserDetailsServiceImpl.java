package com.example.barbershop.infrastructure.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.barbershop.infrastructure.adapter.out.persistence.jpa.UserJpaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserJpaRepository userJpaRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userJpaRepository.findByEmail(email)
                .map(u -> new org.springframework.security.core.userdetails.User(
                        u.getEmail(),
                        u.getHashPassword(),
                        List.of(new SimpleGrantedAuthority(
                                "ROLE_" + u.getRole().getName().name()))
                ))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + email));
    }
}
