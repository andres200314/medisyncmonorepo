package com.medisync.medisync.application.usecases.inventario;

import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.exceptions.MedicamentoNotFoundException;
import com.medisync.medisync.domain.models.Inventario;
import com.medisync.medisync.domain.models.Medicamento;
import com.medisync.medisync.domain.repositories.IInventarioRepository;
import com.medisync.medisync.domain.repositories.IMedicamentoRepository;

import java.util.UUID;

public class EstablecerStockUseCase {

    private final IInventarioRepository inventarioRepository;
    private final IMedicamentoRepository medicamentoRepository;

    public EstablecerStockUseCase(
            IInventarioRepository inventarioRepository,
            IMedicamentoRepository medicamentoRepository) {
        this.inventarioRepository = inventarioRepository;
        this.medicamentoRepository = medicamentoRepository;
    }

    public Inventario ejecutar(UUID gestorId, UUID medicamentoId, int cantidad) {
        Inventario inventario = inventarioRepository.findByGestorId(gestorId)
                .orElseThrow(() -> new BusinessRuleViolationException("Inventario no encontrado"));

        Medicamento medicamento = medicamentoRepository.findById(medicamentoId)
                .orElseThrow(() -> new MedicamentoNotFoundException(medicamentoId.toString()));

        inventario.establecerStock(medicamento, cantidad);

        return inventarioRepository.save(inventario);
    }
}