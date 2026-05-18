package com.medisync.medisync.application.usecases.gestor;

import java.util.List;

import com.medisync.medisync.domain.models.Gestor;
import com.medisync.medisync.domain.repositories.IGestorRepository;

public class ObtenerGestoresUseCase {

    private final IGestorRepository gestorRepository;

    public ObtenerGestoresUseCase(IGestorRepository gestorRepository) {
        this.gestorRepository = gestorRepository;
    }

    public List<Gestor> ejecutar() {
        return gestorRepository.findAll();
    }
}