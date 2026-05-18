package com.medisync.medisync.adapters.in.web.dto.medicamento;


import com.medisync.medisync.domain.models.Medicamento;

import java.util.UUID;

public record MedicamentoResponseDTO (
     UUID id,
     String nombre,
     Boolean requiereFormula,
     String descripcion
) {
    public static MedicamentoResponseDTO from(Medicamento medicamento) {
        return new MedicamentoResponseDTO(
                medicamento.getId(),
                medicamento.getNombre(),
                medicamento.getRequiereFormula(),
                medicamento.getDescripcion()
        );
    }
}
