package com.medisync.medisync.domain.valueObjects;

import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.valueobjects.Email;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void deberiaCrearEmailValido() {
        // ACT
        Email email = Email.of("farmacia@central.com");

        // ASSERT
        assertEquals("farmacia@central.com", email.valor());
    }

    @Test
    void deberiaConvertirEmailAMinusculas() {
        // ACT
        Email email = Email.of("Farmacia@Central.com");

        // ASSERT
        assertEquals("farmacia@central.com", email.valor());
    }

    @Test
    void deberiaAceptarEmailConSubdominio() {
        // ACT
        Email email = Email.of("contacto@farmacia.central.com");

        // ASSERT
        assertEquals("contacto@farmacia.central.com", email.valor());
    }

    @Test
    void deberiaAceptarEmailConPuntosEnLocal() {
        // ACT
        Email email = Email.of("farmacia.central@dominio.com");

        // ASSERT
        assertEquals("farmacia.central@dominio.com", email.valor());
    }

    @Test
    void deberiaAceptarEmailConMas() {
        // ACT
        Email email = Email.of("farmacia+ventas@central.com");

        // ASSERT
        assertEquals("farmacia+ventas@central.com", email.valor());
    }

    @Test
    void deberiaAceptarEmailConNumeros() {
        // ACT
        Email email = Email.of("farmacia123@central.com");

        // ASSERT
        assertEquals("farmacia123@central.com", email.valor());
    }

    @Test
    void deberiaAceptarEmailConGuion() {
        // ACT
        Email email = Email.of("farmacia-central@dominio.com");

        // ASSERT
        assertEquals("farmacia-central@dominio.com", email.valor());
    }

    @Test
    void deberiaAceptarEmailConGuionBajo() {
        // ACT
        Email email = Email.of("farmacia_central@dominio.com");

        // ASSERT
        assertEquals("farmacia_central@dominio.com", email.valor());
    }

    @Test
    void deberiaLanzarExcepcionSiEmailEsNulo() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Email.of(null)
        );
        assertEquals("El email no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiEmailEstaVacio() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Email.of("")
        );
        assertEquals("El email no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiEmailEsSoloEspacios() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Email.of("   ")
        );
        assertEquals("El email no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiEmailNoTieneArroba() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Email.of("farmaciacentral.com")
        );
        assertEquals("El email no tiene un formato válido: farmaciacentral.com", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiEmailNoTienePunto() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Email.of("farmacia@central")
        );
        assertEquals("El email no tiene un formato válido: farmacia@central", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiEmailTieneEspacios() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Email.of("farmacia@central .com")
        );
        assertEquals("El email no tiene un formato válido: farmacia@central .com", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiEmailTieneArrobaRepetida() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Email.of("farmacia@@central.com")
        );
        assertEquals("El email no tiene un formato válido: farmacia@@central.com", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiEmailTerminaConPunto() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Email.of("farmacia@central.")
        );
        assertEquals("El email no tiene un formato válido: farmacia@central.", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiDominioEsMuyCorto() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Email.of("farmacia@central.c")
        );
        assertEquals("El email no tiene un formato válido: farmacia@central.c", exception.getMessage());
    }
}