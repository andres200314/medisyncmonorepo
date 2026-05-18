package com.medisync.medisync.domain.valueObjects;

import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.valueobjects.Telefono;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TelefonoTest {

    @Test
    void deberiaCrearTelefonoValido() {
        // ACT
        Telefono telefono = Telefono.of("3123456789");

        // ASSERT
        assertEquals("3123456789", telefono.valor());
    }

    @Test
    void deberiaAceptarTelefonoConPrefijoCelular() {
        // ACT
        Telefono telefono = Telefono.of("3001234567");

        // ASSERT
        assertEquals("3001234567", telefono.valor());
    }

    @Test
    void deberiaAceptarTelefonoFijo() {
        // ACT
        Telefono telefono = Telefono.of("6012345678");

        // ASSERT
        assertEquals("6012345678", telefono.valor());
    }

    @Test
    void deberiaAceptarTelefonoConPrefijoFijoDeOtraCiudad() {
        // ACT
        Telefono telefono1 = Telefono.of("4123456789");
        Telefono telefono2 = Telefono.of("5123456789");
        Telefono telefono3 = Telefono.of("6123456789");

        // ASSERT
        assertEquals("4123456789", telefono1.valor());
        assertEquals("5123456789", telefono2.valor());
        assertEquals("6123456789", telefono3.valor());
    }

    @Test
    void deberiaLanzarExcepcionSiTelefonoEsNulo() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Telefono.of(null)
        );
        assertEquals("El teléfono no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiTelefonoEstaVacio() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Telefono.of("")
        );
        assertEquals("El teléfono no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiTelefonoEsSoloEspacios() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Telefono.of("   ")
        );
        assertEquals("El teléfono no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiTelefonoTieneMenosDe10Digitos() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Telefono.of("31234567")
        );
        assertEquals("El teléfono debe tener 10 dígitos y no empezar con 0: 31234567", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiTelefonoTieneMasDe10Digitos() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Telefono.of("31234567890")
        );
        assertEquals("El teléfono debe tener 10 dígitos y no empezar con 0: 31234567890", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiTelefonoContieneLetras() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Telefono.of("312345678A")
        );
        assertEquals("El teléfono debe tener 10 dígitos y no empezar con 0: 312345678A", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiTelefonoContieneCaracteresEspeciales() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Telefono.of("312-345-6789")
        );
        assertEquals("El teléfono debe tener 10 dígitos y no empezar con 0: 312-345-6789", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiTelefonoContieneEspacios() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Telefono.of("312 345 6789")
        );
        assertEquals("El teléfono debe tener 10 dígitos y no empezar con 0: 312 345 6789", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiTelefonoEmpiezaConCero() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Telefono.of("0123456789")
        );
        assertEquals("El teléfono debe tener 10 dígitos y no empezar con 0: 0123456789", exception.getMessage());
    }
}