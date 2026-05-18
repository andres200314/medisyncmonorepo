package com.medisync.medisync.domain.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.medisync.medisync.domain.models.Inventario;

public interface IInventarioRepository {

    Inventario save(Inventario inventario);

    void deleteById(UUID id);

    Optional<Inventario> findById(UUID id);

    List<Inventario> findAll();

    Optional<Inventario> findByGestorId(UUID gestorId);
}