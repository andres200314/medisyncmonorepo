package com.medisync.medisync.adapters.in.web.dto.medicamento;

import java.math.BigDecimal;

public record MedicamentoAlInventarioRequestDTO(
        String nombre,
        Boolean requiereFormula,
        String descripcion,
        Integer cantidadInicial,
        BigDecimal precioUnitario
) {}
