package com.medisync.medisync.adapters.in.web.dto.inventario;

import com.medisync.medisync.domain.valueobjects.ItemInventario;
import java.math.BigDecimal;
import java.util.UUID;

public record ItemInventarioResponseDTO(
        UUID medicamentoId,
        String medicamentoNombre,
        Integer cantidad,
        BigDecimal precioUnitario
) {
    public static ItemInventarioResponseDTO from(ItemInventario item) {
        return new ItemInventarioResponseDTO(
                item.medicamento().getId(),
                item.medicamento().getNombre(),
                item.cantidad().valor(),
                item.precioUnitario().valor()
        );
    }
}