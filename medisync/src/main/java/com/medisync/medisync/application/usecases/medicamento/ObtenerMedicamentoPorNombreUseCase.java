package com.medisync.medisync.application.usecases.medicamento;

import com.medisync.medisync.domain.models.Medicamento;
import com.medisync.medisync.domain.repositories.IMedicamentoRepository;

import java.util.List;

public class ObtenerMedicamentoPorNombreUseCase {
    private final IMedicamentoRepository medicamentoRepository;

    public ObtenerMedicamentoPorNombreUseCase(IMedicamentoRepository medicamentoRepository) {
        this.medicamentoRepository = medicamentoRepository;
    }

    public List<Medicamento> ejecutar(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return List.of();
        }
        return medicamentoRepository.findByNombreContainingIgnoreCase(nombre);
    }
}
