package com.medisync.medisync.domain.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.medisync.medisync.domain.models.Gestor;


public interface IGestorRepository {
    Gestor save(Gestor gestor);
    void deleteById(UUID id);
    Optional<Gestor> findById(UUID id);
    List<Gestor> findAll();
    Optional<Gestor> findByEmail(String email);
}
