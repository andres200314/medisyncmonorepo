package com.medisync.medisync.application.usecases.inventario;

import java.util.List;

import com.medisync.medisync.domain.models.Inventario;
import com.medisync.medisync.domain.repositories.IInventarioRepository;

public class ObtenerInventariosUseCase {

    private final IInventarioRepository inventarioRepository;

    public ObtenerInventariosUseCase(IInventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    public List<Inventario> ejecutar() {
        return inventarioRepository.findAll();
    }
}