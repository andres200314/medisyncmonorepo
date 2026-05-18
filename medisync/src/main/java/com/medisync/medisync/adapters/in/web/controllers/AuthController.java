package com.medisync.medisync.adapters.in.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medisync.medisync.adapters.in.web.dto.gestor.AuthResponseDTO;
import com.medisync.medisync.adapters.in.web.dto.gestor.LoginRequestDTO;
import com.medisync.medisync.adapters.in.web.dto.gestor.RegisterRequestDTO;
import com.medisync.medisync.application.usecases.gestor.LoginUseCase;
import com.medisync.medisync.application.usecases.gestor.RegistrarGestorUseCase;
import com.medisync.medisync.domain.models.Gestor;
import com.medisync.medisync.domain.repositories.IGestorRepository;
import com.medisync.medisync.infrastructure.security.JwtService;

import lombok.RequiredArgsConstructor;

@Tag(name = "Autenticación", description = "Registro y login de gestores")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegistrarGestorUseCase registrarGestorUseCase;
    private final IGestorRepository gestorRepository;
    private final JwtService jwtService;

    @Operation(summary = "Iniciar sesión", description = "Autentica un gestor y retorna un token JWT")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
        String token = loginUseCase.ejecutar(request.email(), request.password());

        String nombre = gestorRepository.findByEmail(request.email())
                .map(g -> g.getNombre().valor())
                .orElse("");

        return ResponseEntity.ok(AuthResponseDTO.of(token, request.email(), nombre));
    }

    @Operation(summary = "Registrar gestor", description = "Crea una nueva farmacia y retorna un token JWT")
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        Gestor gestor = registrarGestorUseCase.ejecutar(
                request.nombre(),
                request.nit(),
                request.direccion(),
                request.telefono(),
                request.email(),
                request.password(),
                request.latitud(),
                request.longitud()
        );

        String token = jwtService.generateToken(gestor.getId(), gestor.getEmail().valor(), "GESTOR");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AuthResponseDTO.of(token, gestor.getEmail().valor(), gestor.getNombre().valor()));
    }
}