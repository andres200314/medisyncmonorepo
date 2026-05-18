package com.medisync.medisync.application.usecases.gestor;

import java.math.BigDecimal;
import java.util.UUID;

import com.medisync.medisync.domain.exceptions.GestorNotFoundException;
import com.medisync.medisync.domain.models.Gestor;
import com.medisync.medisync.domain.repositories.IGestorRepository;
import com.medisync.medisync.domain.valueobjects.*;

public class ActualizarGestorUseCase {

    private final IGestorRepository gestorRepository;

    public ActualizarGestorUseCase(IGestorRepository gestorRepository) {
        this.gestorRepository = gestorRepository;
    }

    public Gestor ejecutar(UUID id, String nombre, String direccion,
                           String telefono, String email,
                           double latitud, double longitud) {

        Gestor gestorExistente = gestorRepository.findById(id)
                .orElseThrow(() -> new GestorNotFoundException(id.toString()));

        Gestor gestorActualizado = Gestor.builder()
                .id(id)
                .nombre(new Nombre(nombre != null ? nombre : gestorExistente.getNombre().valor()))
                .nit(gestorExistente.getNit())
                .direccion(direccion != null ? direccion : gestorExistente.getDireccion())
                .telefono(new Telefono(telefono != null ? telefono : gestorExistente.getTelefono().valor()))
                .email(new Email(email != null ? email : gestorExistente.getEmail().valor()))
                .passwordHash(gestorExistente.getPasswordHash())
                .coordenadas(new Coordenadas(BigDecimal.valueOf(latitud), BigDecimal.valueOf(longitud)))
                .estado(gestorExistente.getEstado())
                .build();

        return gestorRepository.save(gestorActualizado);
    }
}