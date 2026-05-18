package com.medisync.medisync.domain.models;

import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MedicamentoTest {

    @Test
    void deberiaCrearMedicamentoValido() {
        // ARRANGE & ACT
        Medicamento medicamento = new Medicamento(
                "paracetamol",
                false,
                "Analgésico y antipirético para el dolor leve"
        );

        // ASSERT
        assertNotNull(medicamento);
        assertEquals("Paracetamol", medicamento.getNombre()); // Normalizado
        assertEquals("Analgésico y antipirético para el dolor leve", medicamento.getDescripcion());
        assertFalse(medicamento.getRequiereFormula());
    }

    @Test
    void deberiaNormalizarNombreCorrectamente() {
        // ARRANGE & ACT
        Medicamento medicamento = new Medicamento(
                "IBUPROFENO",
                false,
                "Antiinflamatorio no esteroideo"
        );

        // ASSERT
        assertEquals("Ibuprofeno", medicamento.getNombre());
    }

    @Test
    void deberiaNormalizarNombreConEspacios() {
        // ARRANGE & ACT
        Medicamento medicamento = new Medicamento(
                "  AMOXICILINA  ",
                true,
                "Antibiótico de amplio espectro para infecciones bacterianas"
        );

        // ASSERT
        assertEquals("Amoxicilina", medicamento.getNombre());
    }

    @Test
    void deberiaLanzarExcepcionSiNombreEsNulo() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> new Medicamento(null, false, "Descripción válida")
        );
        assertEquals("El nombre no puede estar vacío", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiNombreEstaVacio() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> new Medicamento("", false, "Descripción válida")
        );
        assertEquals("El nombre no puede estar vacío", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiNombreTieneMenosDe3Caracteres() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> new Medicamento("ab", false, "Descripción válida")
        );
        assertEquals("El nombre debe tener al menos 3 caracteres", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiNombreContieneNumeros() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> new Medicamento("Paracetamol123", false, "Descripción válida")
        );
        assertEquals("El nombre solo puede contener letras", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiDescripcionEsNula() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> new Medicamento("Ibuprofeno", false, null)
        );
        assertEquals("La descripción no puede estar vacía", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiDescripcionEstaVacia() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> new Medicamento("Ibuprofeno", false, "")
        );
        assertEquals("La descripción no puede estar vacía", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiMedicamentoConFormulaTieneDescripcionCorta() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> new Medicamento("Amoxicilina", true, "Descripción")
        );
        assertEquals("Un medicamento con fórmula debe tener una descripción detallada", exception.getMessage());
    }

    @Test
    void deberiaAceptarMedicamentoConFormulaSiDescripcionTieneAlMenos20Caracteres() {
        // ARRANGE & ACT
        Medicamento medicamento = new Medicamento(
                "Amoxicilina",
                true,
                "Antibiótico de amplio espectro para infecciones bacterianas"
        );

        // ASSERT
        assertNotNull(medicamento);
        assertTrue(medicamento.getRequiereFormula());
    }

    @Test
    void deberiaAceptarNombresConTildesYÑ() {
        // ARRANGE & ACT
        Medicamento medicamento = new Medicamento(
                "ibuprofeno",
                false,
                "Antiinflamatorio no esteroideo"
        );

        // ASSERT
        assertEquals("Ibuprofeno", medicamento.getNombre());
    }

    @Test
    void deberiaMantenerIdExistente() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        Medicamento medicamento = Medicamento.builder()
                .id(id)
                .nombre("Paracetamol")
                .requiereFormula(false)
                .descripcion("Analgésico")
                .build();

        // ACT & ASSERT
        assertEquals(id, medicamento.getId());
    }
}