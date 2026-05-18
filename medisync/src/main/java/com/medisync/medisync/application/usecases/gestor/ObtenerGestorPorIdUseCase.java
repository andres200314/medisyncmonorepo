package com.medisync.medisync.application.usecases.gestor;


import com.medisync.medisync.domain.models.Gestor;
import com.medisync.medisync.domain.exceptions.GestorNotFoundException;
import com.medisync.medisync.domain.repositories.IGestorRepository;

import java.util.UUID;

public class ObtenerGestorPorIdUseCase {

    private final IGestorRepository gestorRepository;

    public ObtenerGestorPorIdUseCase(IGestorRepository gestorRepository) {
        this.gestorRepository = gestorRepository;
    }

    public Gestor ejecutar(UUID id) {
        return gestorRepository.findById(id)
                .orElseThrow(() -> new GestorNotFoundException(id.toString()));
    }
}
