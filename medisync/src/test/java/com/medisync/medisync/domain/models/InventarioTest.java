package com.medisync.medisync.domain.models;

import com.medisync.medisync.domain.enums.EstadoGestor;
import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InventarioTest {

    private Gestor gestorActivo;
    private Gestor gestorInactivo;
    private Gestor gestorSuspendido;
    private Medicamento ibuprofeno;
    private Medicamento amoxicilina;
    private Medicamento paracetamol;
    private Cantidad cantidad10;
    private Cantidad cantidad20;
    private Precio precio100;
    private Precio precio200;

    @BeforeEach
    void setUp() {
        // Crear gestores
        gestorActivo = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(Nombre.of("Farmacia Central"))
                .nit(Nit.of("123456789-1"))
                .email(Email.of("farmacia@central.com"))
                .coordenadas(Coordenadas.of(6.2442, -75.5812))
                .estado(EstadoGestor.ACTIVO)
                .build();

        gestorInactivo = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(Nombre.of("Farmacia Inactiva"))
                .nit(Nit.of("987654321-0"))
                .email(Email.of("inactiva@farmacia.com"))
                .estado(EstadoGestor.INACTIVO)
                .build();

        gestorSuspendido = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(Nombre.of("Farmacia Suspendida"))
                .nit(Nit.of("111222333-4"))
                .email(Email.of("suspendida@farmacia.com"))
                .estado(EstadoGestor.SUSPENDIDO)
                .build();

        // Crear medicamentos
        ibuprofeno = Medicamento.builder()
                .id(UUID.randomUUID())
                .nombre("Ibuprofeno")
                .requiereFormula(false)
                .descripcion("Antiinflamatorio no esteroideo para dolor e inflamación")
                .build();

        amoxicilina = Medicamento.builder()
                .id(UUID.randomUUID())
                .nombre("Amoxicilina")
                .requiereFormula(true)
                .descripcion("Antibiótico de amplio espectro para infecciones bacterianas")
                .build();

        paracetamol = Medicamento.builder()
                .id(UUID.randomUUID())
                .nombre("Paracetamol")
                .requiereFormula(false)
                .descripcion("Analgésico y antipirético para fiebre y dolor leve")
                .build();

        // Crear cantidades y precios
        cantidad10 = Cantidad.of(10);
        cantidad20 = Cantidad.of(20);
        precio100 = Precio.of(BigDecimal.valueOf(100.00));
        precio200 = Precio.of(BigDecimal.valueOf(200.00));
    }

    @Test
    void deberiaCrearInventarioVacio() {
        // ARRANGE & ACT
        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestorActivo)
                .items(new java.util.ArrayList<>())
                .build();

        // ASSERT
        assertNotNull(inventario);
        assertTrue(inventario.getItems().isEmpty());
    }

    @Test
    void deberiaAgregarMedicamentoNuevoAlInventario() {
        // ARRANGE
        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestorActivo)
                .build();

        // ACT
        inventario.agregarMedicamento(ibuprofeno, cantidad10, precio100);

        // ASSERT
        assertEquals(1, inventario.getItems().size());
        assertEquals(ibuprofeno.getId(), inventario.getItems().get(0).medicamento().getId());
        assertEquals(10, inventario.getItems().get(0).cantidad().valor());

        assertEquals(0, BigDecimal.valueOf(100.00)
                .compareTo(inventario.getItems().get(0).precioUnitario().valor()));
    }

    @Test
    void deberiaSumarStockCuandoAgregaMedicamentoExistente() {
        // ARRANGE
        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestorActivo)
                .build();

        inventario.agregarMedicamento(ibuprofeno, cantidad10, precio100);

        // ACT
        inventario.agregarMedicamento(ibuprofeno, cantidad10, precio100);

        // ASSERT
        assertEquals(1, inventario.getItems().size());
        assertEquals(20, inventario.getItems().get(0).cantidad().valor());
    }

    @Test
    void deberiaLanzarExcepcionAlAgregarMismoMedicamentoConPrecioDiferente() {
        // ARRANGE
        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestorActivo)
                .build();

        inventario.agregarMedicamento(ibuprofeno, cantidad10, precio100);

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> inventario.agregarMedicamento(ibuprofeno, cantidad10, precio200)
        );
        assertEquals("No se puede agregar el mismo medicamento con diferente precio.", exception.getMessage());
    }

    @Test
    void deberiaReducirStockCorrectamente() {
        // ARRANGE
        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestorActivo)
                .build();

        inventario.agregarMedicamento(ibuprofeno, cantidad20, precio100);

        // ACT
        inventario.reducirStock(ibuprofeno, 5);

        // ASSERT
        assertEquals(15, inventario.getItems().get(0).cantidad().valor());
    }

    @Test
    void deberiaLanzarExcepcionAlReducirStockInsuficiente() {
        // ARRANGE
        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestorActivo)
                .build();

        inventario.agregarMedicamento(ibuprofeno, cantidad10, precio100);

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> inventario.reducirStock(ibuprofeno, 20)
        );
        assertEquals("No hay suficiente stock para reducir", exception.getMessage());
    }

    @Test
    void deberiaQuitarMedicamentoDelInventario() {
        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestorActivo)
                .build();

        inventario.agregarMedicamento(ibuprofeno, cantidad10, precio100);
        inventario.agregarMedicamento(paracetamol, cantidad20, precio100);

        inventario.quitarMedicamento(ibuprofeno);

        assertEquals(1, inventario.getItems().size());
        assertEquals(paracetamol.getId(), inventario.getItems().get(0).medicamento().getId());
    }

    @Test
    void deberiaLanzarExcepcionAlQuitarMedicamentoNoExistente() {
        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestorActivo)
                .build();

        inventario.agregarMedicamento(ibuprofeno, cantidad10, precio100);

        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> inventario.quitarMedicamento(amoxicilina)
        );
        assertEquals("El medicamento no existe en el inventario", exception.getMessage());
        assertEquals(1, inventario.getItems().size());
    }

    @Test
    void deberiaLanzarExcepcionAlReducirStockDeMedicamentoNoExistente() {
        // ARRANGE
        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestorActivo)
                .build();

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> inventario.reducirStock(ibuprofeno, 5)
        );
        assertEquals("El medicamento no existe en el inventario", exception.getMessage());
    }

    @Test
    void deberiaCambiarPrecioCorrectamente() {
        // ARRANGE
        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestorActivo)
                .build();

        inventario.agregarMedicamento(ibuprofeno, cantidad10, precio100);

        // ACT
        inventario.cambiarPrecio(ibuprofeno, precio200);

        // ASSERT
        // ✅ Usar compareTo para BigDecimal
        assertEquals(0, BigDecimal.valueOf(200.00)
                .compareTo(inventario.getItems().getFirst().precioUnitario().valor()));
    }

    @Test
    void deberiaLanzarExcepcionAlCambiarPrecioDeMedicamentoNoExistente() {
        // ARRANGE
        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestorActivo)
                .build();

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> inventario.cambiarPrecio(ibuprofeno, precio200)
        );
        assertEquals("El medicamento no existe en el inventario", exception.getMessage());
    }

    @Test
    void deberiaVerificarStockCorrectamente() {
        // ARRANGE
        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestorActivo)
                .build();

        inventario.agregarMedicamento(ibuprofeno, cantidad10, precio100);

        // ACT & ASSERT
        assertTrue(inventario.tieneStock(ibuprofeno));
        assertFalse(inventario.tieneStock(amoxicilina));
    }

    @Test
    void deberiaBuscarItemCorrectamente() {
        // ARRANGE
        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestorActivo)
                .build();

        inventario.agregarMedicamento(ibuprofeno, cantidad10, precio100);

        // ACT
        ItemInventario item = inventario.buscarItem(ibuprofeno);

        // ASSERT
        assertNotNull(item);
        assertEquals(ibuprofeno.getId(), item.medicamento().getId());
    }

    @Test
    void deberiaRetornarNullSiItemNoExiste() {
        // ARRANGE
        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestorActivo)
                .build();

        // ACT
        ItemInventario item = inventario.buscarItem(ibuprofeno);

        // ASSERT
        assertNull(item);
    }

    @Test
    void deberiaObtenerItemsConStock() {
        // ARRANGE
        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestorActivo)
                .build();

        inventario.agregarMedicamento(ibuprofeno, cantidad10, precio100);
        inventario.agregarMedicamento(amoxicilina, Cantidad.of(0), precio200);
        inventario.agregarMedicamento(paracetamol, cantidad20, precio100);

        // ACT
        var itemsConStock = inventario.obtenerItemsConStock();

        // ASSERT
        assertEquals(2, itemsConStock.size());
        assertTrue(itemsConStock.stream().anyMatch(i -> i.medicamento().getNombre().equals("Ibuprofeno")));
        assertTrue(itemsConStock.stream().anyMatch(i -> i.medicamento().getNombre().equals("Paracetamol")));
        assertFalse(itemsConStock.stream().anyMatch(i -> i.medicamento().getNombre().equals("Amoxicilina")));
    }

    @Test
    void deberiaLanzarExcepcionAlAgregarMedicamentoConGestorNulo() {
        // ARRANGE
        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(null)
                .build();

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> inventario.agregarMedicamento(ibuprofeno, cantidad10, precio100)
        );
        assertEquals("El inventario debe tener un gestor", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionAlAgregarMedicamentoConGestorInactivo() {
        // ARRANGE
        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestorInactivo)
                .build();

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> inventario.agregarMedicamento(ibuprofeno, cantidad10, precio100)
        );
        assertEquals("El gestor debe estar activo para modificar el inventario", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionAlAgregarMedicamentoConGestorSuspendido() {
        // ARRANGE
        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestorSuspendido)
                .build();

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> inventario.agregarMedicamento(ibuprofeno, cantidad10, precio100)
        );
        assertEquals("El gestor debe estar activo para modificar el inventario", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionAlAgregarMedicamentoNulo() {
        // ARRANGE
        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestorActivo)
                .build();

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> inventario.agregarMedicamento(null, cantidad10, precio100)
        );
        assertEquals("El medicamento no puede ser nulo", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionAlReducirStockConGestorInactivo() {
        // ARRANGE
        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestorInactivo)
                .build();

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> inventario.reducirStock(ibuprofeno, 5)
        );
        assertEquals("El gestor debe estar activo para modificar el inventario", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionAlCambiarPrecioConGestorInactivo() {
        // ARRANGE
        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestorInactivo)
                .build();

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> inventario.cambiarPrecio(ibuprofeno, precio200)
        );
        assertEquals("El gestor debe estar activo para modificar el inventario", exception.getMessage());
    }

    @Test
    void deberiaAgregarMultiplesMedicamentos() {
        // ARRANGE
        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestorActivo)
                .build();

        // ACT
        inventario.agregarMedicamento(ibuprofeno, cantidad10, precio100);
        inventario.agregarMedicamento(amoxicilina, cantidad20, precio200);
        inventario.agregarMedicamento(paracetamol, cantidad10, precio100);

        // ASSERT
        assertEquals(3, inventario.getItems().size());
        assertEquals(10, inventario.getItems().get(0).cantidad().valor());
        assertEquals(20, inventario.getItems().get(1).cantidad().valor());
        assertEquals(10, inventario.getItems().get(2).cantidad().valor());
    }

    @Test
    void deberiaVerificarDisponibilidadParaUsuarios() {
        // ARRANGE
        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestorActivo)
                .build();

        inventario.agregarMedicamento(ibuprofeno, cantidad10, precio100);

        // ACT & ASSERT
        assertTrue(inventario.estaDisponibleParaUsuarios());
    }

    @Test
    void noDeberiaEstarDisponibleParaUsuariosSiGestorNoEsVisible() {
        // ARRANGE
        // Crear inventario con gestor inactivo (sin agregar medicamentos)
        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestorInactivo)
                .build();


        // ACT & ASSERT
        assertFalse(inventario.estaDisponibleParaUsuarios());
    }

    @Test
    void noDeberiaEstarDisponibleParaUsuariosSiNoTieneStock() {
        // ARRANGE
        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestorActivo)
                .build();

        inventario.agregarMedicamento(ibuprofeno, Cantidad.of(0), precio100);

        // ACT & ASSERT
        assertFalse(inventario.estaDisponibleParaUsuarios());
    }
}