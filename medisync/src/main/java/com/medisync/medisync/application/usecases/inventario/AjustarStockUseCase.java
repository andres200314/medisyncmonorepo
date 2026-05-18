package com.medisync.medisync.application.usecases.inventario;

import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.exceptions.MedicamentoNotFoundException;
import com.medisync.medisync.domain.models.Inventario;
import com.medisync.medisync.domain.models.Medicamento;
import com.medisync.medisync.domain.repositories.IInventarioRepository;
import com.medisync.medisync.domain.repositories.IMedicamentoRepository;

import java.util.UUID;

public class AjustarStockUseCase {

    private final IInventarioRepository inventarioRepository;
    private final IMedicamentoRepository medicamentoRepository;

    public AjustarStockUseCase(
            IInventarioRepository inventarioRepository,
            IMedicamentoRepository medicamentoRepository) {
        this.inventarioRepository = inventarioRepository;
        this.medicamentoRepository = medicamentoRepository;
    }

    public Inventario ejecutar(UUID gestorId, UUID medicamentoId, int delta) {
        Inventario inventario = inventarioRepository.findByGestorId(gestorId)
                .orElseThrow(() -> new BusinessRuleViolationException("Inventario no encontrado"));

        Medicamento medicamento = medicamentoRepository.findById(medicamentoId)
                .orElseThrow(() -> new MedicamentoNotFoundException(medicamentoId.toString()));

        inventario.ajustarStock(medicamento, delta);

        return inventarioRepository.save(inventario);
    }
}