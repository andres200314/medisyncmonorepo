package com.medisync.medisync.adapters.in.web.dto.medicamento;

public record MedicamentoRequestDTO (
    String nombre,
    Boolean requiereFormula,
    String descripcion
) {}
