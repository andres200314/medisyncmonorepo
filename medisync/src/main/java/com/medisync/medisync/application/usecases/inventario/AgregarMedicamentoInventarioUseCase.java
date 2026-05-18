package com.medisync.medisync.application.usecases.inventario;

import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.exceptions.MedicamentoNotFoundException;
import com.medisync.medisync.domain.models.Inventario;
import com.medisync.medisync.domain.models.Medicamento;
import com.medisync.medisync.domain.repositories.IGestorRepository;
import com.medisync.medisync.domain.repositories.IInventarioRepository;
import com.medisync.medisync.domain.repositories.IMedicamentoRepository;
import com.medisync.medisync.domain.valueobjects.Cantidad;
import com.medisync.medisync.domain.valueobjects.Precio;

import java.math.BigDecimal;
import java.util.UUID;

public class AgregarMedicamentoInventarioUseCase {

    private final IInventarioRepository inventarioRepository;
    private final IMedicamentoRepository medicamentoRepository;
    private final IGestorRepository gestorRepository;

    public AgregarMedicamentoInventarioUseCase(
            IInventarioRepository inventarioRepository,
            IMedicamentoRepository medicamentoRepository,
            IGestorRepository gestorRepository) {
        this.inventarioRepository = inventarioRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.gestorRepository = gestorRepository;
    }

    public Inventario ejecutar(UUID gestorId, UUID medicamentoId, int cantidad, BigDecimal precioUnitario) {

        if (gestorId == null || medicamentoId == null) {
            throw new BusinessRuleViolationException("Los IDs no pueden ser nulos");
        }

        gestorRepository.findById(gestorId)
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "El gestor con id " + gestorId + " no existe"
                ));

        Inventario inventario = inventarioRepository.findByGestorId(gestorId)
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "El gestor no tiene inventario"
                ));

        Medicamento medicamento = medicamentoRepository.findById(medicamentoId)
                .orElseThrow(() -> new MedicamentoNotFoundException(
                        "El medicamento con id " + medicamentoId + " no existe"
                ));

        inventario.agregarMedicamento(
                medicamento,
                Cantidad.of(cantidad),
                Precio.of(precioUnitario)
        );

        return inventarioRepository.save(inventario);
    }
}