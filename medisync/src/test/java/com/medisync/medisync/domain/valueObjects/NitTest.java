package com.medisync.medisync.domain.valueObjects;

import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.valueobjects.Nit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NitTest {

    @Test
    void deberiaCrearNitValidoCon9DigitosYDV() {
        // ACT
        Nit nit = Nit.of("123456789-1");

        // ASSERT
        assertEquals("123456789-1", nit.valor());
    }

    @Test
    void deberiaCrearNitValidoCon10DigitosYDV() {
        // ACT
        Nit nit = Nit.of("1234567890-1");

        // ASSERT
        assertEquals("1234567890-1", nit.valor());
    }

    @Test
    void deberiaAceptarNitConDiferentesDigitosVerificadores() {
        // ACT
        Nit nit1 = Nit.of("123456789-2");
        Nit nit2 = Nit.of("123456789-3");
        Nit nit3 = Nit.of("123456789-9");

        // ASSERT
        assertEquals("123456789-2", nit1.valor());
        assertEquals("123456789-3", nit2.valor());
        assertEquals("123456789-9", nit3.valor());
    }

    @Test
    void deberiaAceptarNitDeFarmaciaComun() {
        // ACT
        Nit nit = Nit.of("800123456-7");

        // ASSERT
        assertEquals("800123456-7", nit.valor());
    }

    @Test
    void deberiaAceptarNitDeEPS() {
        // ACT
        Nit nit = Nit.of("901234567-8");

        // ASSERT
        assertEquals("901234567-8", nit.valor());
    }

    @Test
    void deberiaLanzarExcepcionSiNitEsNulo() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Nit.of(null)
        );
        assertEquals("El NIT no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiNitEstaVacio() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Nit.of("")
        );
        assertEquals("El NIT no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiNitEsSoloEspacios() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Nit.of("   ")
        );
        assertEquals("El NIT no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiNitNoTieneDigitoVerificador() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Nit.of("123456789")
        );
        assertEquals("El NIT no tiene un formato válido: 123456789", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiNitTieneMenosDe9Digitos() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Nit.of("12345678-1")
        );
        assertEquals("El NIT no tiene un formato válido: 12345678-1", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiNitTieneMasDe10Digitos() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Nit.of("12345678901-1")
        );
        assertEquals("El NIT no tiene un formato válido: 12345678901-1", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiNitTieneDigitoVerificadorConMasDeUnDigito() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Nit.of("123456789-12")
        );
        assertEquals("El NIT no tiene un formato válido: 123456789-12", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiNitTieneGuionIncorrecto() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Nit.of("123456789_1")
        );
        assertEquals("El NIT no tiene un formato válido: 123456789_1", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiNitTieneLetras() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Nit.of("ABCDEFGHI-1")
        );
        assertEquals("El NIT no tiene un formato válido: ABCDEFGHI-1", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiNitTieneEspacios() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Nit.of("123456789 -1")
        );
        assertEquals("El NIT no tiene un formato válido: 123456789 -1", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiNitTieneDigitoVerificadorNoNumerico() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Nit.of("123456789-A")
        );
        assertEquals("El NIT no tiene un formato válido: 123456789-A", exception.getMessage());
    }
}