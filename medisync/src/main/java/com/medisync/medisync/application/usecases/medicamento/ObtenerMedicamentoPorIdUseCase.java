package com.medisync.medisync.application.usecases.medicamento;

import java.util.UUID;



import com.medisync.medisync.domain.exceptions.MedicamentoNotFoundException;
import com.medisync.medisync.domain.models.Medicamento;
import com.medisync.medisync.domain.repositories.IMedicamentoRepository;


public class ObtenerMedicamentoPorIdUseCase {

    private final IMedicamentoRepository medicamentoRepository;

    public ObtenerMedicamentoPorIdUseCase(IMedicamentoRepository medicamentoRepository) {
        this.medicamentoRepository = medicamentoRepository;
    }

    public Medicamento ejecutar(UUID id) {
    return medicamentoRepository.findById(id)
            .orElseThrow(() -> new MedicamentoNotFoundException(id.toString()));
    }
}