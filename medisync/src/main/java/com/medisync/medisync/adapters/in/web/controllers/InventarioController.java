package com.medisync.medisync.adapters.in.web.controllers;

import com.medisync.medisync.adapters.in.web.dto.inventario.DisponibilidadResponseDTO;
import com.medisync.medisync.adapters.in.web.dto.inventario.InventarioResponseDTO;
import com.medisync.medisync.adapters.in.web.dto.inventario.ItemInventarioRequestDTO;
import com.medisync.medisync.adapters.in.web.dto.inventario.ItemInventarioResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import com.medisync.medisync.application.usecases.inventario.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Inventario", description = "Gestión de inventario de farmacias")
@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final ObtenerInventariosUseCase obtenerInventariosUseCase;
    private final ObtenerInventarioPorGestorUseCase obtenerInventarioPorGestorIdUseCase;
    private final AgregarMedicamentoInventarioUseCase agregarMedicamentoInventarioUseCase;
    private final QuitarMedicamentoInventarioUseCase quitarMedicamentoInventarioUseCase;
    private final AjustarStockUseCase ajustarStockUseCase;
    private final EstablecerStockUseCase establecerStockUseCase;
    private final CambiarPrecioUseCase cambiarPrecioUseCase;
    private final BuscarDisponibilidadMedicamentoUseCase buscarDisponibilidadMedicamentoUseCase;

    @Operation(summary = "Obtener todos los inventarios", description = "Retorna todos los inventarios de todas las farmacias")
    @GetMapping
    public ResponseEntity<List<InventarioResponseDTO>> obtenerTodos() {
        var inventarios = obtenerInventariosUseCase.ejecutar().stream()
                .map(InventarioResponseDTO::from)
                .toList();
        return ResponseEntity.ok(inventarios);
    }

    @Operation(summary = "Obtener mi inventario", description = "Retorna el inventario del gestor autenticado. Requiere token.")
    @GetMapping("/mi-inventario")
    public ResponseEntity<InventarioResponseDTO> obtenerMiInventario(Authentication authentication) {
        UUID gestorId = (UUID) authentication.getPrincipal();
        var inventario = obtenerInventarioPorGestorIdUseCase.ejecutar(gestorId);
        return ResponseEntity.ok(InventarioResponseDTO.from(inventario));
    }

    @Operation(summary = "Agregar medicamento", description = "Agrega un medicamento al inventario del gestor autenticado. Si ya existe suma el stock. Requiere token.")
    @PostMapping("/medicamentos")
    public ResponseEntity<InventarioResponseDTO> agregarMedicamento(
            Authentication authentication,
            @RequestBody ItemInventarioRequestDTO request) {
        UUID gestorId = (UUID) authentication.getPrincipal();
        var inventario = agregarMedicamentoInventarioUseCase.ejecutar(
                gestorId,
                request.medicamentoId(),
                request.cantidad(),
                request.precioUnitario()
        );
        return ResponseEntity.ok(InventarioResponseDTO.from(inventario));
    }

    @Operation(summary = "Quitar medicamento del inventario", description = "Elimina el ítem del inventario de la farmacia autenticada. El medicamento permanece en el catálogo global. Requiere token.")
    @DeleteMapping("/medicamentos/{medicamentoId}")
    public ResponseEntity<InventarioResponseDTO> quitarMedicamentoDelInventario(
            Authentication authentication,
            @PathVariable UUID medicamentoId) {
        UUID gestorId = (UUID) authentication.getPrincipal();
        var inventario = quitarMedicamentoInventarioUseCase.ejecutar(gestorId, medicamentoId);
        return ResponseEntity.ok(InventarioResponseDTO.from(inventario));
    }

    @Operation(summary = "Ajustar stock", description = "Ajuste relativo de stock. Número positivo suma, negativo resta. Requiere token.")
    @PatchMapping("/medicamentos/{medicamentoId}/stock/ajustar")
    public ResponseEntity<InventarioResponseDTO> ajustarStock(
            Authentication authentication,
            @PathVariable UUID medicamentoId,
            @RequestParam int cantidad) {
        UUID gestorId = (UUID) authentication.getPrincipal();
        var inventario = ajustarStockUseCase.ejecutar(gestorId, medicamentoId, cantidad);
        return ResponseEntity.ok(InventarioResponseDTO.from(inventario));
    }

    @Operation(summary = "Establecer stock", description = "Establece el stock de forma absoluta. Reemplaza el valor actual. Requiere token.")
    @PutMapping("/medicamentos/{medicamentoId}/stock/establecer")
    public ResponseEntity<InventarioResponseDTO> establecerStock(
            Authentication authentication,
            @PathVariable UUID medicamentoId,
            @RequestParam int cantidad) {
        UUID gestorId = (UUID) authentication.getPrincipal();
        var inventario = establecerStockUseCase.ejecutar(gestorId, medicamentoId, cantidad);
        return ResponseEntity.ok(InventarioResponseDTO.from(inventario));
    }

    @Operation(summary = "Cambiar precio", description = "Actualiza el precio unitario de un medicamento en el inventario del gestor autenticado. Requiere token.")
    @PatchMapping("/medicamentos/{medicamentoId}/precio")
    public ResponseEntity<InventarioResponseDTO> cambiarPrecio(
            Authentication authentication,
            @PathVariable UUID medicamentoId,
            @RequestParam BigDecimal precioUnitario) {
        UUID gestorId = (UUID) authentication.getPrincipal();
        var inventario = cambiarPrecioUseCase.ejecutar(gestorId, medicamentoId, precioUnitario);
        return ResponseEntity.ok(InventarioResponseDTO.from(inventario));
    }

    @Operation(summary = "Buscar disponibilidad", description = "Busca farmacias que tienen stock de un medicamento por nombre. No requiere token.")
    @GetMapping("/disponibilidad")
    public ResponseEntity<List<DisponibilidadResponseDTO>> buscarDisponibilidad(
            @RequestParam String medicamento) {

        var inventarios = buscarDisponibilidadMedicamentoUseCase.ejecutar(medicamento);

        var response = inventarios.stream()
                .map(inv -> {
                    var itemsFiltrados = inv.getItems().stream()
                            .filter(item -> item.medicamento().getNombre()
                                    .toLowerCase()
                                    .contains(medicamento.toLowerCase()))
                            .filter(item -> item.tieneStock())
                            .map(ItemInventarioResponseDTO::from)
                            .toList();

                    return DisponibilidadResponseDTO.from(inv, itemsFiltrados);
                })
                .filter(dto -> !dto.items().isEmpty())
                .toList();

        return ResponseEntity.ok(response);
    }
}