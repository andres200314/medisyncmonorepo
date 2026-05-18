package com.medisync.medisync.application.usecases.gestor;

import com.medisync.medisync.application.usecases.inventario.CrearInventarioUseCase;
import com.medisync.medisync.domain.models.Gestor;
import com.medisync.medisync.domain.models.Inventario;
import com.medisync.medisync.domain.repositories.IGestorRepository;
import com.medisync.medisync.domain.services.IPasswordEncoder;

public class RegistrarGestorUseCase {

    private final IGestorRepository gestorRepository;
    private final IPasswordEncoder passwordEncoder;
    private final CrearInventarioUseCase crearInventarioUseCase;

    public RegistrarGestorUseCase(IGestorRepository gestorRepository,
                                  IPasswordEncoder passwordEncoder,
                                  CrearInventarioUseCase crearInventarioUseCase) {
        this.gestorRepository = gestorRepository;
        this.passwordEncoder = passwordEncoder;
        this.crearInventarioUseCase = crearInventarioUseCase;
    }

    public Gestor ejecutar(String nombre,
                           String nit,
                           String direccion,
                           String telefono,
                           String email,
                           String password,
                           double latitud,
                           double longitud) {
        String passwordHash = passwordEncoder.encode(password);
        Gestor gestor = Gestor.crear(nombre, nit, direccion, telefono, email, passwordHash, latitud, longitud);

        gestor.activar();

        Gestor gestorGuardado = gestorRepository.save(gestor);

        Inventario inventario = Inventario.builder()
                .gestor(gestorGuardado)
                .build();
        crearInventarioUseCase.ejecutar(inventario);

        return gestorGuardado;
    }
}