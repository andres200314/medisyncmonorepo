package com.medisync.medisync.application.usecases.inventario;

import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.models.Inventario;
import com.medisync.medisync.domain.models.Medicamento;
import com.medisync.medisync.domain.repositories.IInventarioRepository;
import com.medisync.medisync.domain.valueobjects.ItemInventario;

import java.util.UUID;

public class ObtenerItemInventarioUseCase {

    private final IInventarioRepository inventarioRepository;

    public ObtenerItemInventarioUseCase(IInventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    public ItemInventario ejecutar(UUID gestorId, UUID medicamentoId) {
        if (gestorId == null) {
            throw new BusinessRuleViolationException("El ID del gestor no puede ser nulo");
        }
        if (medicamentoId == null) {
            throw new BusinessRuleViolationException("El ID del medicamento no puede ser nulo");
        }

        Inventario inventario = inventarioRepository.findByGestorId(gestorId)
                .orElseThrow(() -> new BusinessRuleViolationException("El gestor no tiene inventario"));

        Medicamento medicamento = Medicamento.builder()
                .id(medicamentoId)
                .build();

        ItemInventario item = inventario.buscarItem(medicamento);

        if (item == null) {
            throw new BusinessRuleViolationException("El medicamento no existe en el inventario");
        }

        return item;
    }
}