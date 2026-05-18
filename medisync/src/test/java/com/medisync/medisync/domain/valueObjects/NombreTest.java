package com.medisync.medisync.domain.valueObjects;

import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.valueobjects.Nombre;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NombreTest {

    @Test
    void deberiaCrearNombreDeGestorValido() {
        // ACT
        Nombre nombre = Nombre.of("Farmacia Central");

        // ASSERT
        assertEquals("Farmacia Central", nombre.valor());
    }

    @Test
    void deberiaNormalizarTrimAlCrear() {
        // ACT
        Nombre nombre = Nombre.of("  Farmacia Central  ");

        // ASSERT
        assertEquals("Farmacia Central", nombre.valor());
    }

    @Test
    void deberiaAceptarNombreDeGestorConAcentos() {
        // ACT
        Nombre nombre = Nombre.of("Farmacía Central");

        // ASSERT
        assertEquals("Farmacía Central", nombre.valor());
    }

    @Test
    void deberiaAceptarNombreDeGestorConÑ() {
        // ACT
        Nombre nombre = Nombre.of("Farmacia Ñañez");

        // ASSERT
        assertEquals("Farmacia Ñañez", nombre.valor());
    }

    @Test
    void deberiaAceptarNombreDeGestorConEspacios() {
        // ACT
        Nombre nombre = Nombre.of("Farmacia Central de Medellín");

        // ASSERT
        assertEquals("Farmacia Central de Medellín", nombre.valor());
    }

    @Test
    void deberiaLanzarExcepcionSiNombreDeGestorEsNulo() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Nombre.of(null)
        );
        assertEquals("El nombre no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiNombreDeGestorEstaVacio() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Nombre.of("")
        );
        assertEquals("El nombre no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiNombreDeGestorEsSoloEspacios() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Nombre.of("   ")
        );
        assertEquals("El nombre no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiNombreDeGestorTieneMenosDe4Caracteres() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Nombre.of("ABC")
        );
        assertEquals("El nombre debe tener al menos 4 caracteres", exception.getMessage());
    }

    @Test
    void deberiaAceptarNombreDeGestorCon4Caracteres() {
        // ACT
        Nombre nombre = Nombre.of("Farm");

        // ASSERT
        assertEquals("Farm", nombre.valor());
    }

    @Test
    void deberiaAceptarNombreDeGestorConNumeros() {
        // ACT
        Nombre nombre = Nombre.of("Farmacia 24 Horas");

        // ASSERT
        assertEquals("Farmacia 24 Horas", nombre.valor());
    }
}