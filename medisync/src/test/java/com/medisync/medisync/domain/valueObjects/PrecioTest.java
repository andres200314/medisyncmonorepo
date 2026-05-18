package com.medisync.medisync.domain.valueObjects;

import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.valueobjects.Precio;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PrecioTest {

    @Test
    void deberiaCrearPrecioValido() {
        // ACT
        Precio precio = Precio.of(BigDecimal.valueOf(12500.50));

        // ASSERT
        assertEquals(0, BigDecimal.valueOf(12500.50).compareTo(precio.valor()));
    }

    @Test
    void deberiaCrearPrecioDesdeDouble() {
        // ACT
        Precio precio = Precio.of(BigDecimal.valueOf(12500.50));

        // ASSERT
        assertEquals(0, BigDecimal.valueOf(12500.50).compareTo(precio.valor()));
    }

    @Test
    void deberiaNormalizarPrecioADosDecimales() {
        // ACT
        Precio precio = Precio.of(BigDecimal.valueOf(12500));

        // ASSERT
        assertEquals(0, BigDecimal.valueOf(12500.00).compareTo(precio.valor()));
        assertEquals(2, precio.valor().scale());
    }

    @Test
    void deberiaRedondearCorrectamente() {
        // ACT
        Precio precio1 = Precio.of(BigDecimal.valueOf(12500.555));
        Precio precio2 = Precio.of(BigDecimal.valueOf(12500.554));

        // ASSERT
        assertEquals(0, BigDecimal.valueOf(12500.56).compareTo(precio1.valor()));
        assertEquals(0, BigDecimal.valueOf(12500.55).compareTo(precio2.valor()));
    }

    @Test
    void deberiaPermitirPrecioCero() {
        // ACT
        Precio precio = Precio.of(BigDecimal.ZERO);

        // ASSERT
        assertTrue(precio.esCero());
        assertEquals(0, BigDecimal.ZERO.compareTo(precio.valor()));
    }

    @Test
    void deberiaLanzarExcepcionSiPrecioEsNulo() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Precio.of((BigDecimal) null)
        );
        assertEquals("El precio no puede ser nulo", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiPrecioEsNegativo() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Precio.of(BigDecimal.valueOf(-100))
        );
        assertEquals("El precio no puede ser negativo", exception.getMessage());
    }

    @Test
    void deberiaSumarCorrectamente() {
        // ARRANGE
        Precio precio1 = Precio.of(BigDecimal.valueOf(100.00));
        Precio precio2 = Precio.of(BigDecimal.valueOf(50.00));

        // ACT
        Precio resultado = precio1.sumar(precio2);

        // ASSERT
        assertEquals(0, BigDecimal.valueOf(150.00).compareTo(resultado.valor()));
    }

    @Test
    void deberiaSumarPreciosConDiferenteEscala() {
        // ARRANGE
        Precio precio1 = Precio.of(BigDecimal.valueOf(100));
        Precio precio2 = Precio.of(BigDecimal.valueOf(50.75));

        // ACT
        Precio resultado = precio1.sumar(precio2);

        // ASSERT
        assertEquals(0, BigDecimal.valueOf(150.75).compareTo(resultado.valor()));
    }

    @Test
    void deberiaMultiplicarCorrectamente() {
        // ARRANGE
        Precio precio = Precio.of(BigDecimal.valueOf(100.00));

        // ACT
        Precio resultado = precio.multiplicar(3);

        // ASSERT
        assertEquals(0, BigDecimal.valueOf(300.00).compareTo(resultado.valor()));
    }

    @Test
    void deberiaMultiplicarPorCero() {
        // ARRANGE
        Precio precio = Precio.of(BigDecimal.valueOf(100.00));

        // ACT
        Precio resultado = precio.multiplicar(0);

        // ASSERT
        assertEquals(0, BigDecimal.valueOf(0.00).compareTo(resultado.valor()));
        assertTrue(resultado.esCero());
    }

    @Test
    void deberiaLanzarExcepcionAlMultiplicarPorCantidadNegativa() {
        // ARRANGE
        Precio precio = Precio.of(BigDecimal.valueOf(100.00));

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> precio.multiplicar(-3)
        );
        assertEquals("La cantidad no puede ser negativa", exception.getMessage());
    }

    @Test
    void deberiaCompararMayorQue() {
        // ARRANGE
        Precio precioMayor = Precio.of(BigDecimal.valueOf(200.00));
        Precio precioMenor = Precio.of(BigDecimal.valueOf(100.00));

        // ASSERT
        assertTrue(precioMayor.esMayorQue(precioMenor));
        assertFalse(precioMenor.esMayorQue(precioMayor));
    }

    @Test
    void noDeberiaConsiderarMayorQueSiSonIguales() {
        // ARRANGE
        Precio precio1 = Precio.of(BigDecimal.valueOf(100.00));
        Precio precio2 = Precio.of(BigDecimal.valueOf(100.00));

        // ASSERT
        assertFalse(precio1.esMayorQue(precio2));
    }

    @Test
    void deberiaIdentificarPrecioCero() {
        // ARRANGE
        Precio precioCero = Precio.of(BigDecimal.ZERO);
        Precio precioPositivo = Precio.of(BigDecimal.valueOf(100));

        // ASSERT
        assertTrue(precioCero.esCero());
        assertFalse(precioPositivo.esCero());
    }

    @Test
    void deberiaMantenerInmutabilidad() {
        // ARRANGE
        Precio original = Precio.of(BigDecimal.valueOf(100.00));
        Precio otro = Precio.of(BigDecimal.valueOf(50.00));

        // ACT
        Precio resultado = original.sumar(otro);

        // ASSERT
        assertEquals(0, BigDecimal.valueOf(100.00).compareTo(original.valor()));
        assertEquals(0, BigDecimal.valueOf(150.00).compareTo(resultado.valor()));
    }

    @Test
    void deberiaEncadenarOperaciones() {
        // ARRANGE
        Precio precio = Precio.of(BigDecimal.valueOf(100.00));
        Precio incremento = Precio.of(BigDecimal.valueOf(50.00));

        // ACT
        Precio resultado = precio
                .sumar(incremento)
                .multiplicar(2);

        // ASSERT
        assertEquals(0, BigDecimal.valueOf(300.00).compareTo(resultado.valor()));
    }

    @Test
    void deberiaFuncionarConPreciosMuyGrandes() {
        // ACT
        Precio precio = Precio.of(new BigDecimal("999999999999.99"));

        // ASSERT
        assertEquals(0, new BigDecimal("999999999999.99").compareTo(precio.valor()));
    }

    @Test
    void deberiaFuncionarConPreciosMuyPequenos() {
        // ACT
        Precio precio = Precio.of(new BigDecimal("0.01"));

        // ASSERT
        assertEquals(0, new BigDecimal("0.01").compareTo(precio.valor()));
    }
}