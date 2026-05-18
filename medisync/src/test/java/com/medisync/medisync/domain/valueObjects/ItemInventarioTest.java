package com.medisync.medisync.domain.valueObjects;

import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.models.Medicamento;
import com.medisync.medisync.domain.valueobjects.Cantidad;
import com.medisync.medisync.domain.valueobjects.ItemInventario;
import com.medisync.medisync.domain.valueobjects.Precio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ItemInventarioTest {

    private Medicamento ibuprofeno;
    private Medicamento amoxicilina;
    private Cantidad cantidad10;
    private Cantidad cantidad20;
    private Precio precio100;
    private Precio precio200;

    @BeforeEach
    void setUp() {
        ibuprofeno = Medicamento.builder()
                .id(UUID.randomUUID())
                .nombre("Ibuprofeno")
                .requiereFormula(false)
                .descripcion("Antiinflamatorio no esteroideo")
                .build();

        amoxicilina = Medicamento.builder()
                .id(UUID.randomUUID())
                .nombre("Amoxicilina")
                .requiereFormula(true)
                .descripcion("Antibiótico de amplio espectro")
                .build();

        cantidad10 = Cantidad.of(10);
        cantidad20 = Cantidad.of(20);
        precio100 = Precio.of(BigDecimal.valueOf(100.00));
        precio200 = Precio.of(BigDecimal.valueOf(200.00));
    }

    @Test
    void deberiaCrearItemInventarioValido() {
        // ACT
        ItemInventario item = new ItemInventario(ibuprofeno, cantidad10, precio100);

        // ASSERT
        assertNotNull(item);
        assertEquals(ibuprofeno, item.medicamento());
        assertEquals(10, item.cantidad().valor());
        assertEquals(0, BigDecimal.valueOf(100.00).compareTo(item.precioUnitario().valor()));
    }

    @Test
    void deberiaLanzarExcepcionSiMedicamentoEsNulo() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> new ItemInventario(null, cantidad10, precio100)
        );
        assertEquals("El medicamento no puede ser nulo", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiCantidadEsNula() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> new ItemInventario(ibuprofeno, null, precio100)
        );
        assertEquals("La cantidad no puede ser nula", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiPrecioEsNulo() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> new ItemInventario(ibuprofeno, cantidad10, null)
        );
        assertEquals("El precio no puede ser nulo", exception.getMessage());
    }

    @Test
    void deberiaAumentarStockCorrectamente() {
        // ARRANGE
        ItemInventario item = new ItemInventario(ibuprofeno, cantidad10, precio100);

        // ACT
        ItemInventario nuevoItem = item.aumentarStock(5);

        // ASSERT
        assertEquals(15, nuevoItem.cantidad().valor());
        assertEquals(ibuprofeno, nuevoItem.medicamento());
        assertEquals(0, BigDecimal.valueOf(100.00).compareTo(nuevoItem.precioUnitario().valor()));
        // Verificar inmutabilidad: el original no cambia
        assertEquals(10, item.cantidad().valor());
    }

    @Test
    void deberiaAumentarStockConCero() {
        // ARRANGE
        ItemInventario item = new ItemInventario(ibuprofeno, cantidad10, precio100);

        // ACT
        ItemInventario nuevoItem = item.aumentarStock(0);

        // ASSERT
        assertEquals(10, nuevoItem.cantidad().valor());
        assertEquals(ibuprofeno, nuevoItem.medicamento());
        assertEquals(0, BigDecimal.valueOf(100.00).compareTo(nuevoItem.precioUnitario().valor()));
    }

    @Test
    void deberiaReducirStockCorrectamente() {
        // ARRANGE
        ItemInventario item = new ItemInventario(ibuprofeno, cantidad10, precio100);

        // ACT
        ItemInventario nuevoItem = item.reducirStock(3);

        // ASSERT
        assertEquals(7, nuevoItem.cantidad().valor());
        assertEquals(ibuprofeno, nuevoItem.medicamento());
        assertEquals(0, BigDecimal.valueOf(100.00).compareTo(nuevoItem.precioUnitario().valor()));
        // Verificar inmutabilidad: el original no cambia
        assertEquals(10, item.cantidad().valor());
    }

    @Test
    void deberiaReducirStockConCero() {
        // ARRANGE
        ItemInventario item = new ItemInventario(ibuprofeno, cantidad10, precio100);

        // ACT
        ItemInventario nuevoItem = item.reducirStock(0);

        // ASSERT
        assertEquals(10, nuevoItem.cantidad().valor());
        assertEquals(ibuprofeno, nuevoItem.medicamento());
        assertEquals(0, BigDecimal.valueOf(100.00).compareTo(nuevoItem.precioUnitario().valor()));
    }

    @Test
    void deberiaCambiarPrecioCorrectamente() {
        // ARRANGE
        ItemInventario item = new ItemInventario(ibuprofeno, cantidad10, precio100);

        // ACT
        ItemInventario nuevoItem = item.cambiarPrecio(precio200);

        // ASSERT
        assertEquals(0, BigDecimal.valueOf(200.00).compareTo(nuevoItem.precioUnitario().valor()));
        assertEquals(ibuprofeno, nuevoItem.medicamento());
        assertEquals(10, nuevoItem.cantidad().valor());
        // Verificar inmutabilidad: el original no cambia
        assertEquals(0, BigDecimal.valueOf(100.00).compareTo(item.precioUnitario().valor()));
    }

    @Test
    void deberiaVerificarTieneStock() {
        // ARRANGE
        ItemInventario itemConStock = new ItemInventario(ibuprofeno, cantidad10, precio100);
        ItemInventario itemSinStock = new ItemInventario(ibuprofeno, Cantidad.of(0), precio100);

        // ACT & ASSERT
        assertTrue(itemConStock.tieneStock());
        assertFalse(itemSinStock.tieneStock());
    }

    @Test
    void deberiaIdentificarMismoMedicamento() {
        // ARRANGE
        ItemInventario item = new ItemInventario(ibuprofeno, cantidad10, precio100);

        // ACT & ASSERT
        assertTrue(item.esDelMismoMedicamento(ibuprofeno));
        assertFalse(item.esDelMismoMedicamento(amoxicilina));
    }

    @Test
    void deberiaMantenerInmutabilidadAlAumentarStock() {
        // ARRANGE
        ItemInventario itemOriginal = new ItemInventario(ibuprofeno, cantidad10, precio100);

        // ACT
        ItemInventario itemModificado = itemOriginal.aumentarStock(5);

        // ASSERT
        assertNotSame(itemOriginal, itemModificado);
        assertEquals(10, itemOriginal.cantidad().valor());
        assertEquals(15, itemModificado.cantidad().valor());
    }

    @Test
    void deberiaMantenerInmutabilidadAlReducirStock() {
        // ARRANGE
        ItemInventario itemOriginal = new ItemInventario(ibuprofeno, cantidad10, precio100);

        // ACT
        ItemInventario itemModificado = itemOriginal.reducirStock(3);

        // ASSERT
        assertNotSame(itemOriginal, itemModificado);
        assertEquals(10, itemOriginal.cantidad().valor());
        assertEquals(7, itemModificado.cantidad().valor());
    }

    @Test
    void deberiaMantenerInmutabilidadAlCambiarPrecio() {
        // ARRANGE
        ItemInventario itemOriginal = new ItemInventario(ibuprofeno, cantidad10, precio100);

        // ACT
        ItemInventario itemModificado = itemOriginal.cambiarPrecio(precio200);

        // ASSERT
        assertNotSame(itemOriginal, itemModificado);
        assertEquals(0, BigDecimal.valueOf(100.00).compareTo(itemOriginal.precioUnitario().valor()));
        assertEquals(0, BigDecimal.valueOf(200.00).compareTo(itemModificado.precioUnitario().valor()));
    }

    @Test
    void deberiaEncadenarOperaciones() {
        // ARRANGE
        ItemInventario item = new ItemInventario(ibuprofeno, cantidad10, precio100);

        // ACT: aumentar stock, luego cambiar precio
        ItemInventario resultado = item
                .aumentarStock(5)
                .cambiarPrecio(precio200);

        // ASSERT
        assertEquals(15, resultado.cantidad().valor());
        assertEquals(0, BigDecimal.valueOf(200.00).compareTo(resultado.precioUnitario().valor()));
        assertEquals(ibuprofeno, resultado.medicamento());
    }

    @Test
    void deberiaLanzarExcepcionAlAumentarConCantidadNegativa() {
        // ARRANGE
        ItemInventario item = new ItemInventario(ibuprofeno, cantidad10, precio100);

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> item.aumentarStock(-5)
        );
        assertEquals("La cantidad a sumar debe ser positiva o cero", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionAlReducirConCantidadNegativa() {
        // ARRANGE
        ItemInventario item = new ItemInventario(ibuprofeno, cantidad10, precio100);

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> item.reducirStock(-3)
        );
        assertEquals("La cantidad a restar debe ser positiva o cero", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionAlReducirMasDeLoDisponible() {
        // ARRANGE
        ItemInventario item = new ItemInventario(ibuprofeno, cantidad10, precio100);

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> item.reducirStock(20)
        );
        assertEquals("No hay suficiente stock. Stock actual: 10, solicitado: 20", exception.getMessage());
    }
}