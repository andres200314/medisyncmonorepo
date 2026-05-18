package com.medisync.medisync.application.usecases.medicamento;

import com.medisync.medisync.domain.models.Medicamento;
import com.medisync.medisync.domain.repositories.IMedicamentoRepository;

public class CrearMedicamentoUseCase {

    private final IMedicamentoRepository medicamentoRepository;

    public CrearMedicamentoUseCase(IMedicamentoRepository medicamentoRepository) {
        this.medicamentoRepository = medicamentoRepository;
    }

    public Medicamento ejecutar(String nombre, Boolean requiereFormula, String descripcion) {
        Medicamento medicamento = Medicamento.crear(nombre, requiereFormula, descripcion);
        return medicamentoRepository.save(medicamento);
    }
}