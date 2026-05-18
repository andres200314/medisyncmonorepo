package com.medisync.medisync.application.usecases.medicamento;

import java.util.List;



import com.medisync.medisync.domain.models.Medicamento;
import com.medisync.medisync.domain.repositories.IMedicamentoRepository;


public class ObtenerMedicamentosUseCase {
    private final IMedicamentoRepository medicamentoRepository;

    public ObtenerMedicamentosUseCase(IMedicamentoRepository medicamentoRepository) {
        this.medicamentoRepository = medicamentoRepository;
    }

    public List<Medicamento> ejecutar() {
        return medicamentoRepository.findAll();
    }
}