package com.medisync.medisync.adapters.in.web.dto.gestor;

public record AuthResponseDTO(
        String token,
        String tipo,
        String email,
        String nombre
) {
    public static AuthResponseDTO of(String token, String email, String nombre) {
        return new AuthResponseDTO(token, "Bearer", email, nombre);
    }
}