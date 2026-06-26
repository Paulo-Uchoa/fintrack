package com.paulouchoa.fintrack.auth.dto;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresInMinutes,
        String name,
        String email) {
}
