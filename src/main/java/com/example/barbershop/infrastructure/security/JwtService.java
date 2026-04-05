package com.example.barbershop.infrastructure.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.example.barbershop.domain.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import java.util.Map;

@Component
public class JwtService {
        private final SecretKey key;
    private final long expirationMs;

    public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claims(Map.of(
                        "role", user.getRole().name(),
                        "isPasswordTemporary", user.isPasswordTemporary()
                ))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean extractIsPasswordTemporary(String token) {
        Object val = parseClaims(token).get("isPasswordTemporary");
        return val != null && (boolean) val;
    }

    public boolean isTokenValid(String token) {
        try { parseClaims(token); return true; }
        catch (JwtException e) { return false; }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
    }
}
