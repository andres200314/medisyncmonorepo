package com.medisync.medisync.domain.valueObjects;

import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.valueobjects.Cantidad;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CantidadTest {

    @Test
    void deberiaCrearCantidadValida() {
        // ACT
        Cantidad cantidad = Cantidad.of(10);

        // ASSERT
        assertEquals(10, cantidad.valor());
    }

    @Test
    void deberiaPermitirCantidadCero() {
        // ACT
        Cantidad cantidad = Cantidad.of(0);

        // ASSERT
        assertEquals(0, cantidad.valor());
        assertTrue(cantidad.esCero());
        assertFalse(cantidad.tieneStock());
        assertFalse(cantidad.esPositiva());
    }

    @Test
    void deberiaLanzarExcepcionSiCantidadEsNegativa() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Cantidad.of(-5)
        );
        assertEquals("La cantidad no puede ser negativa", exception.getMessage());
    }

    @Test
    void deberiaVerificarTieneStock() {
        // ACT
        Cantidad cantidadConStock = Cantidad.of(10);
        Cantidad cantidadSinStock = Cantidad.of(0);

        // ASSERT
        assertTrue(cantidadConStock.tieneStock());
        assertFalse(cantidadSinStock.tieneStock());
        assertTrue(cantidadConStock.esPositiva());
        assertFalse(cantidadSinStock.esPositiva());
    }

    @Test
    void deberiaVerificarEsCero() {
        // ACT
        Cantidad cantidadCero = Cantidad.of(0);
        Cantidad cantidadPositiva = Cantidad.of(10);

        // ASSERT
        assertTrue(cantidadCero.esCero());
        assertFalse(cantidadPositiva.esCero());
    }

    @Test
    void deberiaSumarCorrectamente() {
        // ARRANGE
        Cantidad cantidad = Cantidad.of(10);

        // ACT
        Cantidad resultado = cantidad.sumar(5);

        // ASSERT
        assertEquals(15, resultado.valor());
    }

    @Test
    void deberiaSumarCero() {
        // ARRANGE
        Cantidad cantidad = Cantidad.of(10);

        // ACT
        Cantidad resultado = cantidad.sumar(0);

        // ASSERT
        assertEquals(10, resultado.valor());
    }

    @Test
    void deberiaLanzarExcepcionAlSumarCantidadNegativa() {
        // ARRANGE
        Cantidad cantidad = Cantidad.of(10);

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> cantidad.sumar(-5)
        );
        assertEquals("La cantidad a sumar debe ser positiva o cero", exception.getMessage());
    }

    @Test
    void deberiaRestarCorrectamente() {
        // ARRANGE
        Cantidad cantidad = Cantidad.of(10);

        // ACT
        Cantidad resultado = cantidad.restar(3);

        // ASSERT
        assertEquals(7, resultado.valor());
    }

    @Test
    void deberiaRestarCero() {
        // ARRANGE
        Cantidad cantidad = Cantidad.of(10);

        // ACT
        Cantidad resultado = cantidad.restar(0);

        // ASSERT
        assertEquals(10, resultado.valor());
    }

    @Test
    void deberiaLanzarExcepcionAlRestarCantidadNegativa() {
        // ARRANGE
        Cantidad cantidad = Cantidad.of(10);

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> cantidad.restar(-3)
        );
        assertEquals("La cantidad a restar debe ser positiva o cero", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionAlRestarMasDeLoDisponible() {
        // ARRANGE
        Cantidad cantidad = Cantidad.of(10);

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> cantidad.restar(15)
        );
        assertEquals("No hay suficiente stock. Stock actual: 10, solicitado: 15", exception.getMessage());
    }

    @Test
    void deberiaRestarExactamenteElStock() {
        // ARRANGE
        Cantidad cantidad = Cantidad.of(10);

        // ACT
        Cantidad resultado = cantidad.restar(10);

        // ASSERT
        assertEquals(0, resultado.valor());
        assertTrue(resultado.esCero());
        assertFalse(resultado.tieneStock());
    }

    @Test
    void deberiaMantenerInmutabilidad() {
        // ARRANGE
        Cantidad original = Cantidad.of(10);

        // ACT
        Cantidad resultado = original.sumar(5);

        // ASSERT
        assertEquals(10, original.valor());
        assertEquals(15, resultado.valor());
        assertNotSame(original, resultado);
    }

    @Test
    void deberiaEncadenarOperaciones() {
        // ARRANGE
        Cantidad cantidad = Cantidad.of(10);

        // ACT
        Cantidad resultado = cantidad
                .sumar(5)
                .restar(3)
                .sumar(2);

        // ASSERT
        assertEquals(14, resultado.valor());
    }

    @Test
    void deberiaFuncionarConCantidadesGrandes() {
        // ACT
        Cantidad cantidad = Cantidad.of(1000000);

        // ASSERT
        assertEquals(1000000, cantidad.valor());
    }

    @Test
    void deberiaConvertirATextoCorrectamente() {
        // ACT
        Cantidad cantidad = Cantidad.of(10);

        // ASSERT
        assertEquals("10", cantidad.toString());
    }
}