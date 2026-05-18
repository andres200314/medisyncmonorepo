package com.medisync.medisync.adapters.out.persistence.mappers;

import org.springframework.stereotype.Component;

import com.medisync.medisync.adapters.out.persistence.entities.MedicamentoEntity;
import com.medisync.medisync.domain.models.Medicamento;

@Component
public class MedicamentoEntityMapper {

    public Medicamento toDomain(MedicamentoEntity entity) {
        return Medicamento.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .requiereFormula(entity.getRequiereFormula())
                .descripcion(entity.getDescripcion())
                .build();
    }

    public MedicamentoEntity toEntity(Medicamento medicamento) {
        return MedicamentoEntity.builder()
                .id(medicamento.getId())
                .nombre(medicamento.getNombre())
                .requiereFormula(medicamento.getRequiereFormula())
                .descripcion(medicamento.getDescripcion())
                .build();
    }
}
