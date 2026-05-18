package com.medisync.medisync.application.usecases.gestor;


import java.util.UUID;

import com.medisync.medisync.domain.exceptions.GestorNotFoundException;
import com.medisync.medisync.domain.repositories.IGestorRepository;
import com.medisync.medisync.domain.repositories.IInventarioRepository;

public class EliminarGestorUseCase {

    private final IGestorRepository gestorRepository;

    public EliminarGestorUseCase(IGestorRepository gestorRepository, IInventarioRepository inventarioRepository) {
        this.gestorRepository = gestorRepository;
    }

    public void ejecutar(UUID id) {
        gestorRepository.findById(id)
                .orElseThrow(() -> new GestorNotFoundException(id.toString()));
        gestorRepository.deleteById(id);
    }
}