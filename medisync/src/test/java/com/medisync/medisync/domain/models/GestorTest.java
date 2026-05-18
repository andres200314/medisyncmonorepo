package com.medisync.medisync.domain.models;

import com.medisync.medisync.domain.enums.EstadoGestor;
import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GestorTest {

    private Nombre nombre;
    private Nit nit;
    private Email email;
    private Telefono telefono;
    private Coordenadas coordenadas;

    @BeforeEach
    void setUp() {
        nombre = Nombre.of("Farmacia Central");
        nit = Nit.of("123456789-1");
        email = Email.of("farmacia@central.com");
        telefono = Telefono.of("3123456789");
        coordenadas = Coordenadas.of(BigDecimal.valueOf(6.2442), BigDecimal.valueOf(-75.5812));
    }

    @Test
    void deberiaCrearGestorActivo() {
        // ARRANGE & ACT
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .coordenadas(coordenadas)
                .estado(EstadoGestor.ACTIVO)
                .build();

        // ASSERT
        assertNotNull(gestor);
        assertEquals(EstadoGestor.ACTIVO, gestor.getEstado());
        assertTrue(gestor.estaActivo());
    }

    @Test
    void deberiaCrearGestorInactivo() {
        // ARRANGE & ACT
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .estado(EstadoGestor.INACTIVO)
                .build();

        // ASSERT
        assertEquals(EstadoGestor.INACTIVO, gestor.getEstado());
        assertFalse(gestor.estaActivo());
    }

    @Test
    void deberiaActivarGestorConUbicacion() {
        // ARRANGE
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .coordenadas(coordenadas)
                .estado(EstadoGestor.INACTIVO)
                .build();

        // ACT
        gestor.activar();

        // ASSERT
        assertEquals(EstadoGestor.ACTIVO, gestor.getEstado());
        assertTrue(gestor.estaActivo());
    }

    @Test
    void deberiaLanzarExcepcionAlActivarGestorSinUbicacion() {
        // ARRANGE
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .estado(EstadoGestor.INACTIVO)
                .build();

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                gestor::activar
        );
        assertEquals("No se puede activar sin ubicación", exception.getMessage());
        assertEquals(EstadoGestor.INACTIVO, gestor.getEstado());
    }

    @Test
    void deberiaLanzarExcepcionAlActivarGestorSuspendido() {
        // ARRANGE
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .coordenadas(coordenadas)
                .estado(EstadoGestor.SUSPENDIDO)
                .build();

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                gestor::activar
        );
        assertEquals("Un gestor suspendido no puede activarse", exception.getMessage());
        assertEquals(EstadoGestor.SUSPENDIDO, gestor.getEstado());
    }

    @Test
    void deberiaDesactivarGestor() {
        // ARRANGE
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .coordenadas(coordenadas)
                .estado(EstadoGestor.ACTIVO)
                .build();

        // ACT
        gestor.desactivar();

        // ASSERT
        assertEquals(EstadoGestor.INACTIVO, gestor.getEstado());
        assertFalse(gestor.estaActivo());
    }

    @Test
    void deberiaLanzarExcepcionAlDesactivarGestorSuspendido() {
        // ARRANGE
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .coordenadas(coordenadas)
                .estado(EstadoGestor.SUSPENDIDO)
                .build();

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                gestor::desactivar
        );
        assertEquals("Un gestor suspendido no puede cambiar estado", exception.getMessage());
        assertEquals(EstadoGestor.SUSPENDIDO, gestor.getEstado());
    }

    @Test
    void deberiaSuspenderGestor() {
        // ARRANGE
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .coordenadas(coordenadas)
                .estado(EstadoGestor.ACTIVO)
                .build();

        // ACT
        gestor.suspender();

        // ASSERT
        assertEquals(EstadoGestor.SUSPENDIDO, gestor.getEstado());
    }

    @Test
    void deberiaSuspenderGestorInactivo() {
        // ARRANGE
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .coordenadas(coordenadas)
                .estado(EstadoGestor.INACTIVO)
                .build();

        // ACT
        gestor.suspender();

        // ASSERT
        assertEquals(EstadoGestor.SUSPENDIDO, gestor.getEstado());
    }

    @Test
    void deberiaLanzarExcepcionAlSuspenderGestorYaSuspendido() {
        // ARRANGE
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .coordenadas(coordenadas)
                .estado(EstadoGestor.SUSPENDIDO)
                .build();

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                gestor::suspender
        );
        assertEquals("Ya está suspendido", exception.getMessage());
    }

    @Test
    void deberiaSerVisibleParaUsuariosCuandoActivoYConUbicacion() {
        // ARRANGE
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .coordenadas(coordenadas)
                .estado(EstadoGestor.ACTIVO)
                .build();

        // ACT & ASSERT
        assertTrue(gestor.esVisibleParaUsuarios());
    }

    @Test
    void noDeberiaSerVisibleParaUsuariosCuandoInactivo() {
        // ARRANGE
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .coordenadas(coordenadas)
                .estado(EstadoGestor.INACTIVO)
                .build();

        // ACT & ASSERT
        assertFalse(gestor.esVisibleParaUsuarios());
    }

    @Test
    void noDeberiaSerVisibleParaUsuariosCuandoSuspendido() {
        // ARRANGE
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .coordenadas(coordenadas)
                .estado(EstadoGestor.SUSPENDIDO)
                .build();

        // ACT & ASSERT
        assertFalse(gestor.esVisibleParaUsuarios());
    }

    @Test
    void noDeberiaSerVisibleParaUsuariosCuandoNoTieneUbicacion() {
        // ARRANGE
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .estado(EstadoGestor.ACTIVO)
                .build();

        // ACT & ASSERT
        assertFalse(gestor.esVisibleParaUsuarios());
    }

    @Test
    void deberiaActualizarUbicacion() {
        // ARRANGE
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .coordenadas(coordenadas)
                .estado(EstadoGestor.ACTIVO)
                .build();

        Coordenadas nuevasCoordenadas = Coordenadas.of(BigDecimal.valueOf(6.2500), BigDecimal.valueOf(-75.6000));

        // ACT
        gestor.actualizarUbicacion(nuevasCoordenadas);

        // ASSERT
        assertEquals(nuevasCoordenadas, gestor.getCoordenadas());
    }

    @Test
    void deberiaLanzarExcepcionAlActualizarUbicacionSiGestorSuspendido() {
        // ARRANGE
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .coordenadas(coordenadas)
                .estado(EstadoGestor.SUSPENDIDO)
                .build();

        Coordenadas nuevasCoordenadas = Coordenadas.of(BigDecimal.valueOf(6.2500), BigDecimal.valueOf(-75.6000));

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> gestor.actualizarUbicacion(nuevasCoordenadas)
        );
        assertEquals("No se puede actualizar ubicación si está suspendido", exception.getMessage());
        assertEquals(coordenadas, gestor.getCoordenadas());
    }

    @Test
    void deberiaCambiarEmail() {
        // ARRANGE
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .coordenadas(coordenadas)
                .estado(EstadoGestor.ACTIVO)
                .build();

        Email nuevoEmail = Email.of("nuevo@farmacia.com");

        // ACT
        gestor.cambiarEmail(nuevoEmail);

        // ASSERT
        assertEquals(nuevoEmail, gestor.getEmail());
    }

    @Test
    void deberiaLanzarExcepcionAlCambiarEmailSiGestorSuspendido() {
        // ARRANGE
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .coordenadas(coordenadas)
                .estado(EstadoGestor.SUSPENDIDO)
                .build();

        Email nuevoEmail = Email.of("nuevo@farmacia.com");

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> gestor.cambiarEmail(nuevoEmail)
        );
        assertEquals("No se puede modificar un gestor suspendido", exception.getMessage());
        assertEquals(email, gestor.getEmail());
    }

    @Test
    void deberiaCambiarPassword() {
        // ARRANGE
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .coordenadas(coordenadas)
                .estado(EstadoGestor.ACTIVO)
                .build();

        String nuevaPasswordHash = "nuevo_hash_seguro";

        // ACT
        gestor.cambiarPassword(nuevaPasswordHash);

        // ASSERT
        assertEquals(nuevaPasswordHash, gestor.getPasswordHash());
    }

    @Test
    void deberiaLanzarExcepcionAlCambiarPasswordSiGestorSuspendido() {
        // ARRANGE
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .coordenadas(coordenadas)
                .estado(EstadoGestor.SUSPENDIDO)
                .build();

        String nuevaPasswordHash = "nuevo_hash_seguro";

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> gestor.cambiarPassword(nuevaPasswordHash)
        );
        assertEquals("No se puede modificar un gestor suspendido", exception.getMessage());
        assertNotEquals(nuevaPasswordHash, gestor.getPasswordHash());
    }

    @Test
    void deberiaLanzarExcepcionAlSetearPasswordNulo() {
        // ARRANGE
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .coordenadas(coordenadas)
                .estado(EstadoGestor.ACTIVO)
                .build();

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> gestor.setPasswordHash(null)
        );
        assertEquals("El password no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionAlSetearPasswordVacio() {
        // ARRANGE
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .coordenadas(coordenadas)
                .estado(EstadoGestor.ACTIVO)
                .build();

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> gestor.setPasswordHash("")
        );
        assertEquals("El password no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void deberiaTenerUbicacionCuandoCoordenadasNoSonNulas() {
        // ARRANGE
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .coordenadas(coordenadas)
                .estado(EstadoGestor.ACTIVO)
                .build();

        // ACT & ASSERT
        assertTrue(gestor.tieneUbicacion());
    }

    @Test
    void noDeberiaTenerUbicacionCuandoCoordenadasSonNulas() {
        // ARRANGE
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .estado(EstadoGestor.ACTIVO)
                .build();

        // ACT & ASSERT
        assertFalse(gestor.tieneUbicacion());
    }

    @Test
    void deberiaPoderIniciarSesionSiEstaActivo() {
        // ARRANGE
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .coordenadas(coordenadas)
                .estado(EstadoGestor.ACTIVO)
                .build();

        // ACT & ASSERT
        assertTrue(gestor.puedeIniciarSesion());
    }

    @Test
    void noDeberiaPoderIniciarSesionSiEstaInactivo() {
        // ARRANGE
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .coordenadas(coordenadas)
                .estado(EstadoGestor.INACTIVO)
                .build();

        // ACT & ASSERT
        assertFalse(gestor.puedeIniciarSesion());
    }

    @Test
    void noDeberiaPoderIniciarSesionSiEstaSuspendido() {
        // ARRANGE
        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .nit(nit)
                .email(email)
                .coordenadas(coordenadas)
                .estado(EstadoGestor.SUSPENDIDO)
                .build();

        // ACT & ASSERT
        assertFalse(gestor.puedeIniciarSesion());
    }
}