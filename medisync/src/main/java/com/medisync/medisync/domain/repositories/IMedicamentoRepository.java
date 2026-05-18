package com.medisync.medisync.domain.repositories;

import com.medisync.medisync.domain.models.Medicamento;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface IMedicamentoRepository {
    Medicamento save(Medicamento medicamento);
    void deleteById(UUID id);
    Optional<Medicamento> findById(UUID id);
    List<Medicamento> findAll();
    List<Medicamento> findByNombreContainingIgnoreCase(String nombre);
}
