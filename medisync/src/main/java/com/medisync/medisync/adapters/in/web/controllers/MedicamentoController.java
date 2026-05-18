package com.medisync.medisync.adapters.in.web.controllers;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.medisync.medisync.adapters.in.web.dto.medicamento.MedicamentoAlInventarioRequestDTO;
import com.medisync.medisync.adapters.in.web.dto.medicamento.MedicamentoRequestDTO;
import com.medisync.medisync.adapters.in.web.dto.medicamento.MedicamentoResponseDTO;
import com.medisync.medisync.application.usecases.medicamento.*;
import com.medisync.medisync.domain.models.Medicamento;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Medicamentos", description = "Gestión de medicamentos")
@RestController
@RequestMapping("/api/medicamentos")
@RequiredArgsConstructor
public class MedicamentoController {

    private final AgregarOActualizarMedicamentoInventarioUseCase agregarOActualizarMedicamentoInventarioUseCase;
    private final ObtenerMedicamentosUseCase obtenerMedicamentosUseCase;
    private final ObtenerMedicamentoPorIdUseCase obtenerMedicamentoPorIdUseCase;
    private final EliminarMedicamentoUseCase eliminarMedicamentoUseCase;
    private final ActualizarMedicamentoUseCase actualizarMedicamentoUseCase;

    @Operation(summary = "Agregar medicamento al inventario", description = "Busca por nombre (sin distinguir mayúsculas); si no existe crea el medicamento en catálogo y lo agrega al inventario del gestor autenticado; si existe solo actualiza inventario. Requiere token.")
    @PostMapping
    public ResponseEntity<MedicamentoResponseDTO> crear(
            Authentication authentication,
            @RequestBody MedicamentoAlInventarioRequestDTO request) {

        UUID gestorId = (UUID) authentication.getPrincipal();

        Medicamento medicamento = agregarOActualizarMedicamentoInventarioUseCase.ejecutar(
                gestorId,
                request.nombre(),
                request.requiereFormula(),
                request.descripcion(),
                request.cantidadInicial(),
                request.precioUnitario()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(MedicamentoResponseDTO.from(medicamento));
    }

    @Operation(summary = "Obtener todos los medicamentos", description = "Retorna el catálogo completo de medicamentos")
    @GetMapping
    public ResponseEntity<List<MedicamentoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(
                obtenerMedicamentosUseCase.ejecutar().stream()
                        .map(MedicamentoResponseDTO::from)
                        .toList()
        );
    }

    @Operation(summary = "Obtener medicamento por ID", description = "Retorna un medicamento por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDTO> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(MedicamentoResponseDTO.from(obtenerMedicamentoPorIdUseCase.ejecutar(id)));
    }

    @Operation(summary = "Actualizar medicamento", description = "Actualiza el nombre y descripción de un medicamento")
    @PutMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDTO> actualizar(
            @PathVariable UUID id,
            @RequestBody MedicamentoRequestDTO request) {
        return ResponseEntity.ok(
                MedicamentoResponseDTO.from(
                        actualizarMedicamentoUseCase.ejecutar(
                                id,
                                request.nombre(),
                                request.descripcion()
                        )
                )
        );
    }

    @Operation(summary = "Eliminar medicamento", description = "Elimina un medicamento del catálogo")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        eliminarMedicamentoUseCase.ejecutar(id);
        return ResponseEntity.noContent().build();
    }
}