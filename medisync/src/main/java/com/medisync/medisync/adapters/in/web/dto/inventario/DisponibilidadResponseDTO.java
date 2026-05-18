package com.medisync.medisync.adapters.in.web.dto.inventario;

import com.medisync.medisync.domain.models.Inventario;

import java.util.List;
import java.util.UUID;

public record DisponibilidadResponseDTO(
        UUID gestorId,
        String gestorNombre,
        String gestorDireccion,
        double latitud,
        double longitud,
        List<ItemInventarioResponseDTO> items
) {
    public static DisponibilidadResponseDTO from(Inventario inventario, List<ItemInventarioResponseDTO> itemsFiltrados) {
        var gestor = inventario.getGestor();
        var coordenadas = gestor.getCoordenadas();
        return new DisponibilidadResponseDTO(
                gestor.getId(),
                gestor.getNombre().valor(),
                gestor.getDireccion(),
                coordenadas.latitud().doubleValue(),
                coordenadas.longitud().doubleValue(),
                itemsFiltrados
        );
    }
}