package com.medisync.medisync.application.usecases.inventario;

import com.medisync.medisync.domain.models.Inventario;
import com.medisync.medisync.domain.repositories.IInventarioRepository;
import com.medisync.medisync.domain.valueobjects.ItemInventario;

import java.util.List;
import java.util.UUID;

public class ListarMedicamentosConStockUseCase {

    private final IInventarioRepository inventarioRepository;

    public ListarMedicamentosConStockUseCase(IInventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    public List<ItemInventario> ejecutar(UUID gestorId) {
        if (gestorId == null) {
            return List.of();
        }

        return inventarioRepository.findByGestorId(gestorId)
                .map(Inventario::obtenerItemsConStock)
                .orElse(List.of());
    }
}