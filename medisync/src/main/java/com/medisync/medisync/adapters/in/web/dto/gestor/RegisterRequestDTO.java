package com.medisync.medisync.adapters.in.web.dto.gestor;

public record RegisterRequestDTO(
        String nombre,
        String nit,
        String direccion,
        String telefono,
        String email,
        String password,
        double latitud,
        double longitud
) {}