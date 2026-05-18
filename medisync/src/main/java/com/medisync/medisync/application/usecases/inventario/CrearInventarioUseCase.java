package com.medisync.medisync.application.usecases.inventario;

import com.medisync.medisync.domain.models.Inventario;
import com.medisync.medisync.domain.repositories.IInventarioRepository;

public class CrearInventarioUseCase {

    private final IInventarioRepository inventarioRepository;

    public CrearInventarioUseCase(IInventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    public Inventario ejecutar(Inventario inventario) {
        return inventarioRepository.save(inventario);
    }
}