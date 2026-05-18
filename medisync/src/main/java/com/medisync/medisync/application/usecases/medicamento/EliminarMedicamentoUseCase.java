package com.medisync.medisync.application.usecases.medicamento;

import java.util.UUID;

import com.medisync.medisync.domain.exceptions.MedicamentoNotFoundException;
import com.medisync.medisync.domain.repositories.IMedicamentoRepository;


public class EliminarMedicamentoUseCase {
    private final IMedicamentoRepository medicamentoRepository;

    public EliminarMedicamentoUseCase (IMedicamentoRepository medicamentoRepository){
        this.medicamentoRepository = medicamentoRepository;
    }

    public void ejecutar(UUID id) {
        medicamentoRepository.findById(id)
                .orElseThrow(() -> new MedicamentoNotFoundException(id.toString()));
        medicamentoRepository.deleteById(id);
    }



}
