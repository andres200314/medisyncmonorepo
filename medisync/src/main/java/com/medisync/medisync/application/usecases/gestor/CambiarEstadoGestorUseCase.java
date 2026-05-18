package com.medisync.medisync.application.usecases.gestor;

import java.util.UUID;

import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.models.Gestor;
import com.medisync.medisync.domain.repositories.IGestorRepository;

public class CambiarEstadoGestorUseCase {

    private final IGestorRepository gestorRepository;

    public CambiarEstadoGestorUseCase(IGestorRepository gestorRepository) {
        this.gestorRepository = gestorRepository;
    }

    public Gestor ejecutar(UUID id) {

        if (id == null) {
            throw new BusinessRuleViolationException("El ID del gestor no puede ser nulo");
        }

        Gestor gestor = gestorRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "El gestor con id " + id + " no existe"
                ));


        switch (gestor.getEstado()) {
            case ACTIVO -> gestor.desactivar();
            case INACTIVO -> gestor.activar();
            default -> throw new BusinessRuleViolationException(
                    "No se puede cambiar el estado del gestor"
            );
        }

        return gestorRepository.save(gestor);
    }
}