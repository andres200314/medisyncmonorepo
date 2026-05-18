package com.medisync.medisync.application.usecases.medicamento;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.medisync.medisync.application.usecases.inventario.AgregarMedicamentoInventarioUseCase;
import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.models.Medicamento;
import com.medisync.medisync.domain.repositories.IMedicamentoRepository;

public class AgregarOActualizarMedicamentoInventarioUseCase {

    private final IMedicamentoRepository medicamentoRepository;
    private final CrearMedicamentoUseCase crearMedicamentoUseCase;
    private final AgregarMedicamentoInventarioUseCase agregarMedicamentoInventarioUseCase;

    public AgregarOActualizarMedicamentoInventarioUseCase(
            IMedicamentoRepository medicamentoRepository,
            CrearMedicamentoUseCase crearMedicamentoUseCase,
            AgregarMedicamentoInventarioUseCase agregarMedicamentoInventarioUseCase) {
        this.medicamentoRepository = medicamentoRepository;
        this.crearMedicamentoUseCase = crearMedicamentoUseCase;
        this.agregarMedicamentoInventarioUseCase = agregarMedicamentoInventarioUseCase;
    }

    public Medicamento ejecutar(
            UUID gestorId,
            String nombre,
            Boolean requiereFormula,
            String descripcion,
            Integer cantidadInicial,
            BigDecimal precioUnitario) {

        if (gestorId == null) {
            throw new BusinessRuleViolationException("El gestor es obligatorio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new BusinessRuleViolationException("El nombre es obligatorio");
        }
        if (cantidadInicial == null) {
            throw new BusinessRuleViolationException("La cantidad inicial es obligatoria");
        }
        if (cantidadInicial < 0) {
            throw new BusinessRuleViolationException("La cantidad inicial no puede ser negativa");
        }
        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleViolationException("El precio unitario no es válido");
        }

        String nombreTrim = nombre.trim();

        List<Medicamento> candidatos =
                medicamentoRepository.findByNombreContainingIgnoreCase(nombreTrim);

        Optional<Medicamento> existente = candidatos.stream()
                .filter(m -> nombreTrim.equalsIgnoreCase(m.getNombre()))
                .findFirst();

        Medicamento medicamento = existente.orElseGet(() -> crearMedicamentoUseCase.ejecutar(
                nombreTrim,
                Boolean.TRUE.equals(requiereFormula),
                descripcion
        ));

        agregarMedicamentoInventarioUseCase.ejecutar(
                gestorId,
                medicamento.getId(),
                cantidadInicial,
                precioUnitario
        );

        return medicamento;
    }
}
