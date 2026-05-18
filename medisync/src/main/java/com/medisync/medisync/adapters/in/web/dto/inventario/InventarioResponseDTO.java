package com.medisync.medisync.adapters.in.web.dto.inventario;

import com.medisync.medisync.domain.models.Inventario;
import java.util.List;
import java.util.UUID;

public record InventarioResponseDTO(
        UUID id,
        UUID gestorId,
        String gestorNombre,
        List<ItemInventarioResponseDTO> items
) {
    public static InventarioResponseDTO from(Inventario inventario) {
        return new InventarioResponseDTO(
                inventario.getId(),
                inventario.getGestor().getId(),
                inventario.getGestor().getNombre().valor(),
                inventario.getItems().stream()
                        .map(ItemInventarioResponseDTO::from)
                        .toList()
        );
    }
}