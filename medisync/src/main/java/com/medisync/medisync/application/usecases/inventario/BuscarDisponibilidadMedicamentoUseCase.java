package com.medisync.medisync.application.usecases.inventario;

import com.medisync.medisync.domain.models.Inventario;
import com.medisync.medisync.domain.repositories.IInventarioRepository;
import com.medisync.medisync.domain.repositories.IMedicamentoRepository;

import java.util.List;

public class BuscarDisponibilidadMedicamentoUseCase {

    private final IInventarioRepository inventarioRepository;
    private final IMedicamentoRepository medicamentoRepository;

    public BuscarDisponibilidadMedicamentoUseCase(
            IInventarioRepository inventarioRepository,
            IMedicamentoRepository medicamentoRepository) {
        this.inventarioRepository = inventarioRepository;
        this.medicamentoRepository = medicamentoRepository;
    }

    public List<Inventario> ejecutar(String nombreMedicamento) {
        var medicamentos = medicamentoRepository.findByNombreContainingIgnoreCase(nombreMedicamento);

        return inventarioRepository.findAll().stream()
                .filter(Inventario::estaDisponibleParaUsuarios)
                .filter(inventario -> medicamentos.stream()
                        .anyMatch(inventario::tieneStock))
                .toList();
    }
}