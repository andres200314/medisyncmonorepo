package com.medisync.medisync.adapters.in.web.dto.inventario;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemInventarioRequestDTO(
        UUID medicamentoId,
        Integer cantidad,
        BigDecimal precioUnitario
) {}