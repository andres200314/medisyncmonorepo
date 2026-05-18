package com.medisync.medisync.adapters.in.web.controllers;

import java.util.List;
import java.util.UUID;

import com.medisync.medisync.application.usecases.gestor.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.medisync.medisync.adapters.in.web.dto.gestor.GestorRequestDTO;
import com.medisync.medisync.adapters.in.web.dto.gestor.GestorResponseDTO;

import lombok.RequiredArgsConstructor;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Gestores", description = "Gestión de farmacias")
@RestController
@RequestMapping("/api/gestores")
@RequiredArgsConstructor
public class GestorController {

    private final ObtenerGestoresUseCase obtenerGestoresUseCase;
    private final ObtenerGestorPorIdUseCase obtenerGestorPorIdUseCase;
    private final ActualizarGestorUseCase actualizarGestorUseCase;
    private final EliminarGestorUseCase eliminarGestorUseCase;
    private final CambiarEstadoGestorUseCase cambiarEstadoGestorUseCase;

    @Operation(summary = "Obtener todas las farmacias", description = "Retorna el listado de todas las farmacias registradas")
    @GetMapping
    public ResponseEntity<List<GestorResponseDTO>> obtenerTodos() {
        var gestores = obtenerGestoresUseCase.ejecutar().stream()
                .map(GestorResponseDTO::from)
                .toList();
        return ResponseEntity.ok(gestores);
    }

    @Operation(summary = "Obtener farmacia por ID", description = "Retorna una farmacia por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<GestorResponseDTO> obtenerPorId(@PathVariable UUID id) {
        var gestor = obtenerGestorPorIdUseCase.ejecutar(id);
        return ResponseEntity.ok(GestorResponseDTO.from(gestor));
    }

    @Operation(summary = "Actualizar perfil", description = "Actualiza los datos del gestor autenticado. Requiere token.")
    @PutMapping
    public ResponseEntity<GestorResponseDTO> actualizar(
            Authentication authentication,
            @RequestBody GestorRequestDTO request) {
        UUID gestorId = (UUID) authentication.getPrincipal();
        var gestor = actualizarGestorUseCase.ejecutar(
                gestorId,
                request.nombre(),
                request.direccion(),
                request.telefono(),
                request.email(),
                request.latitud(),
                request.longitud()
        );
        return ResponseEntity.ok(GestorResponseDTO.from(gestor));
    }

    @Operation(summary = "Cambiar estado", description = "Alterna el estado del gestor autenticado entre ACTIVO e INACTIVO. Requiere token.")
    @PatchMapping("/estado")
    public ResponseEntity<GestorResponseDTO> cambiarEstado(Authentication authentication) {
        UUID gestorId = (UUID) authentication.getPrincipal();
        var gestor = cambiarEstadoGestorUseCase.ejecutar(gestorId);
        return ResponseEntity.ok(GestorResponseDTO.from(gestor));
    }

    @Operation(summary = "Eliminar cuenta", description = "Elimina la cuenta del gestor autenticado. Requiere token.")
    @DeleteMapping
    public ResponseEntity<Void> eliminar(Authentication authentication) {
        UUID gestorId = (UUID) authentication.getPrincipal();
        eliminarGestorUseCase.ejecutar(gestorId);
        return ResponseEntity.noContent().build();
    }
}