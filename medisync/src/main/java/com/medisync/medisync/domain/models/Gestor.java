package com.medisync.medisync.domain.models;

import com.medisync.medisync.domain.enums.EstadoGestor;
import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.valueobjects.*;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gestor {

    private UUID id;
    @Setter
    private Nombre nombre;
    private Nit nit;
    @Setter
    private String direccion;
    @Setter
    private Telefono telefono;
    private Email email;
    private String passwordHash;
    private Coordenadas coordenadas;
    private EstadoGestor estado;

    public static Gestor crear(
            String nombre,
            String nit,
            String direccion,
            String telefono,
            String email,
            String passwordHash,
            double latitud,
            double longitud) {
        return Gestor.builder()
                .id(null)
                .nombre(new Nombre(nombre))
                .nit(new Nit(nit))
                .direccion(direccion)
                .telefono(new Telefono(telefono))
                .email(new Email(email))
                .passwordHash(passwordHash)
                .coordenadas(Coordenadas.of(latitud, longitud))
                .estado(EstadoGestor.INACTIVO)
                .build();
    }


    public void activar() {
        if (this.estado == EstadoGestor.SUSPENDIDO) {
            throw new BusinessRuleViolationException("Un gestor suspendido no puede activarse");
        }
        if (!tieneUbicacion()) {
            throw new BusinessRuleViolationException("No se puede activar sin ubicación");
        }
        this.estado = EstadoGestor.ACTIVO;
    }

    public void desactivar() {
        if (this.estado == EstadoGestor.SUSPENDIDO) {
            throw new BusinessRuleViolationException("Un gestor suspendido no puede cambiar estado");
        }
        this.estado = EstadoGestor.INACTIVO;
    }

    public void suspender() {
        if (this.estado == EstadoGestor.SUSPENDIDO) {
            throw new BusinessRuleViolationException("Ya está suspendido");
        }
        this.estado = EstadoGestor.SUSPENDIDO;
    }

    public boolean esVisibleParaUsuarios() {
        return this.estado == EstadoGestor.ACTIVO && tieneUbicacion();
    }

    public boolean estaActivo() {
        return this.estado == EstadoGestor.ACTIVO;
    }

    public boolean tieneUbicacion() {
        return this.coordenadas != null;
    }

    public void actualizarUbicacion(Coordenadas coordenadas) {
        if (this.estado == EstadoGestor.SUSPENDIDO) {
            throw new BusinessRuleViolationException("No se puede actualizar ubicación si está suspendido");
        }
        this.coordenadas = coordenadas;
    }

    public boolean puedeIniciarSesion() {
        return this.estado == EstadoGestor.ACTIVO;
    }

    public void cambiarEmail(Email email) {
        if (this.estado == EstadoGestor.SUSPENDIDO) {
            throw new BusinessRuleViolationException("No se puede modificar un gestor suspendido");
        }
        this.email = email;
    }

    public void cambiarPassword(String passwordHash) {
        if (this.estado == EstadoGestor.SUSPENDIDO) {
            throw new BusinessRuleViolationException("No se puede modificar un gestor suspendido");
        }
        this.passwordHash = passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new BusinessRuleViolationException("El password no puede ser nulo o vacío");
        }
        this.passwordHash = passwordHash;
    }

}