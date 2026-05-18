package com.medisync.medisync.application.usecases.inventario;

import com.medisync.medisync.domain.enums.EstadoGestor;
import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.models.Gestor;
import com.medisync.medisync.domain.models.Inventario;
import com.medisync.medisync.domain.models.Medicamento;
import com.medisync.medisync.domain.repositories.IInventarioRepository;
import com.medisync.medisync.domain.valueobjects.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObtenerItemInventarioUseCaseTest {

    @Mock
    private IInventarioRepository inventarioRepository;

    @InjectMocks
    private ObtenerItemInventarioUseCase obtenerItemInventarioUseCase;

    @Test
    void deberiaRetornarItemCuandoExiste() {
        // ARRANGE
        UUID gestorId = UUID.randomUUID();
        UUID medicamentoId = UUID.randomUUID();

        Gestor gestor = Gestor.builder()
                .id(gestorId)
                .nombre(Nombre.of("Farmacia Central"))
                .estado(EstadoGestor.ACTIVO)
                .build();

        Medicamento ibuprofeno = Medicamento.builder()
                .id(medicamentoId)
                .nombre("Ibuprofeno")
                .requiereFormula(false)
                .build();

        ItemInventario item = new ItemInventario(
                ibuprofeno,
                Cantidad.of(50),
                Precio.of(BigDecimal.valueOf(12500.00))
        );

        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestor)
                .items(List.of(item))
                .build();

        when(inventarioRepository.findByGestorId(gestorId))
                .thenReturn(Optional.of(inventario));

        // ACT
        ItemInventario resultado = obtenerItemInventarioUseCase.ejecutar(gestorId, medicamentoId);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(medicamentoId, resultado.medicamento().getId());
        assertEquals("Ibuprofeno", resultado.medicamento().getNombre());
        assertEquals(50, resultado.cantidad().valor());
        assertEquals(0, BigDecimal.valueOf(12500.00).compareTo(resultado.precioUnitario().valor()));

        verify(inventarioRepository, times(1)).findByGestorId(gestorId);
    }

    @Test
    void deberiaLanzarExcepcionCuandoGestorNoTieneInventario() {
        // ARRANGE
        UUID gestorId = UUID.randomUUID();
        UUID medicamentoId = UUID.randomUUID();

        when(inventarioRepository.findByGestorId(gestorId))
                .thenReturn(Optional.empty());

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> obtenerItemInventarioUseCase.ejecutar(gestorId, medicamentoId)
        );

        assertEquals("El gestor no tiene inventario", exception.getMessage());
        verify(inventarioRepository, times(1)).findByGestorId(gestorId);
    }

    @Test
    void deberiaLanzarExcepcionCuandoMedicamentoNoExisteEnInventario() {
        // ARRANGE
        UUID gestorId = UUID.randomUUID();
        UUID medicamentoIdExistente = UUID.randomUUID();
        UUID medicamentoIdNoExistente = UUID.randomUUID();

        Gestor gestor = Gestor.builder()
                .id(gestorId)
                .nombre(Nombre.of("Farmacia Central"))
                .estado(EstadoGestor.ACTIVO)
                .build();

        Medicamento ibuprofeno = Medicamento.builder()
                .id(medicamentoIdExistente)
                .nombre("Ibuprofeno")
                .requiereFormula(false)
                .build();

        ItemInventario item = new ItemInventario(
                ibuprofeno,
                Cantidad.of(50),
                Precio.of(BigDecimal.valueOf(12500.00))
        );

        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestor)
                .items(List.of(item))
                .build();

        when(inventarioRepository.findByGestorId(gestorId))
                .thenReturn(Optional.of(inventario));

        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> obtenerItemInventarioUseCase.ejecutar(gestorId, medicamentoIdNoExistente)
        );

        assertEquals("El medicamento no existe en el inventario", exception.getMessage());
        verify(inventarioRepository, times(1)).findByGestorId(gestorId);
    }

    @Test
    void deberiaLanzarExcepcionCuandoGestorIdEsNulo() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> obtenerItemInventarioUseCase.ejecutar(null, UUID.randomUUID())
        );

        assertEquals("El ID del gestor no puede ser nulo", exception.getMessage());
        verify(inventarioRepository, never()).findByGestorId(any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoMedicamentoIdEsNulo() {
        // ACT & ASSERT
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> obtenerItemInventarioUseCase.ejecutar(UUID.randomUUID(), null)
        );

        assertEquals("El ID del medicamento no puede ser nulo", exception.getMessage());
        verify(inventarioRepository, never()).findByGestorId(any());
    }
}