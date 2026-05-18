package com.medisync.medisync.domain.valueObjects;

import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.valueobjects.Coordenadas;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CoordenadasTest {

    @Test
    void deberiaCrearCoordenadasValidas() {
        // ACT
        Coordenadas coordenadas = Coordenadas.of(6.2442, -75.5812);

        // ASSERT
        assertEquals(0, BigDecimal.valueOf(6.2442).compareTo(coordenadas.latitud()));
        assertEquals(0, BigDecimal.valueOf(-75.5812).compareTo(coordenadas.longitud()));
    }

    @Test
    void deberiaCrearCoordenadasConBigDecimal() {
        // ACT
        Coordenadas coordenadas = Coordenadas.of(
                BigDecimal.valueOf(6.2442),
                BigDecimal.valueOf(-75.5812)
        );

        // ASSERT
        assertEquals(0, BigDecimal.valueOf(6.2442).compareTo(coordenadas.latitud()));
        assertEquals(0, BigDecimal.valueOf(-75.5812).compareTo(coordenadas.longitud()));
    }

    @Test
    void deberiaAceptarCoordenadasDeMedellin() {
        // ACT
        Coordenadas coordenadas = Coordenadas.of(6.2442, -75.5812);

        // ASSERT
        assertNotNull(coordenadas);
    }

    @Test
    void deberiaAceptarCoordenadasDeBogota() {
        // ACT
        Coordenadas coordenadas = Coordenadas.of(4.7110, -74.0721);

        // ASSERT
        assertNotNull(coordenadas);
    }

    @Test
    void deberiaAceptarCoordenadasDeCali() {
        // ACT
        Coordenadas coordenadas = Coordenadas.of(3.4516, -76.5320);

        // ASSERT
        assertNotNull(coordenadas);
    }

    @Test
    void deberiaAceptarCoordenadasEnLimiteLatitudNegativa() {
        // ACT
        Coordenadas coordenadas = Coordenadas.of(-90.0, 0.0);

        // ASSERT
        assertEquals(0, BigDecimal.valueOf(-90.0).compareTo(coordenadas.latitud()));
    }

    @Test
    void deberiaAceptarCoordenadasEnLimiteLatitudPositiva() {
        // ACT
        Coordenadas coordenadas = Coordenadas.of(90.0, 0.0);

        // ASSERT
        assertEquals(0, BigDecimal.valueOf(90.0).compareTo(coordenadas.latitud()));
    }

    @Test
    void deberiaAceptarCoordenadasEnLimiteLongitudNegativa() {
        // ACT
        Coordenadas coordenadas = Coordenadas.of(0.0, -180.0);

        // ASSERT
        assertEquals(0, BigDecimal.valueOf(-180.0).compareTo(coordenadas.longitud()));
    }

    @Test
    void deberiaAceptarCoordenadasEnLimiteLongitudPositiva() {
        // ACT
        Coordenadas coordenadas = Coordenadas.of(0.0, 180.0);

        // ASSERT
        assertEquals(0, BigDecimal.valueOf(180.0).compareTo(coordenadas.longitud()));
    }

    @Test
    void deberiaLanzarExcepcionSiLatitudEsNula() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Coordenadas.of((BigDecimal) null, BigDecimal.valueOf(-75.5812))
        );
        assertEquals("Las coordenadas no pueden ser nulas", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiLongitudEsNula() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Coordenadas.of(BigDecimal.valueOf(6.2442), null)
        );
        assertEquals("Las coordenadas no pueden ser nulas", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiLatitudEsMenorQueMenos90() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Coordenadas.of(-91.0, 0.0)
        );
        assertEquals("La latitud debe estar entre -90 y 90", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiLatitudEsMayorQue90() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Coordenadas.of(91.0, 0.0)
        );
        assertEquals("La latitud debe estar entre -90 y 90", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiLongitudEsMenorQueMenos180() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Coordenadas.of(0.0, -181.0)
        );
        assertEquals("La longitud debe estar entre -180 y 180", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiLongitudEsMayorQue180() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> Coordenadas.of(0.0, 181.0)
        );
        assertEquals("La longitud debe estar entre -180 y 180", exception.getMessage());
    }
}