package com.medisync.medisync.adapters.in.web.dto.gestor;

import java.util.UUID;
import com.medisync.medisync.domain.models.Gestor;

public record GestorResponseDTO(
        UUID id,
        String nombre,
        String nit,
        String direccion,
        String telefono,
        String email,
        String estado,
        double latitud,
        double longitud
) {
    public static GestorResponseDTO from(Gestor gestor) {
        return new GestorResponseDTO(
                gestor.getId(),
                gestor.getNombre().valor(),
                gestor.getNit().valor(),
                gestor.getDireccion(),
                gestor.getTelefono().valor(),
                gestor.getEmail().valor(),
                gestor.getEstado().name(),
                gestor.getCoordenadas().latitud().doubleValue(),
                gestor.getCoordenadas().longitud().doubleValue()
        );
    }
}