package com.medisync.medisync.adapters.in.web.dto.inventario;

import java.util.List;
import java.util.UUID;

public record InventarioRequestDTO(
        UUID gestorId,
        List<ItemInventarioRequestDTO> items
) {}