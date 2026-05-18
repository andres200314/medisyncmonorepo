package com.medisync.medisync.application.usecases.inventario;

import com.medisync.medisync.domain.enums.EstadoGestor;
import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.models.Gestor;
import com.medisync.medisync.domain.models.Inventario;
import com.medisync.medisync.domain.models.Medicamento;
import com.medisync.medisync.domain.repositories.IGestorRepository;
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
class ObtenerInventarioPorGestorUseCaseTest {

    @Mock
    private IInventarioRepository inventarioRepository;

    @Mock
    private IGestorRepository gestorRepository;

    @InjectMocks
    private ObtenerInventarioPorGestorUseCase obtenerInventarioPorGestorUseCase;

    @Test
    void deberiaRetornarInventarioCuandoGestorTieneInventario() {
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

        when(gestorRepository.findById(gestorId))
                .thenReturn(Optional.of(gestor));

        when(inventarioRepository.findByGestorId(gestorId))
                .thenReturn(Optional.of(inventario));

        Inventario resultado = obtenerInventarioPorGestorUseCase.ejecutar(gestorId);

        assertNotNull(resultado);
        assertEquals(gestorId, resultado.getGestor().getId());
        assertEquals(1, resultado.getItems().size());

        verify(gestorRepository).findById(gestorId);
        verify(inventarioRepository).findByGestorId(gestorId);
    }

    @Test
    void deberiaLanzarExcepcionCuandoGestorNoExiste() {
        UUID gestorId = UUID.randomUUID();

        when(gestorRepository.findById(gestorId))
                .thenReturn(Optional.empty());

        assertThrows(BusinessRuleViolationException.class, () ->
                obtenerInventarioPorGestorUseCase.ejecutar(gestorId)
        );

        verify(gestorRepository).findById(gestorId);
        verifyNoInteractions(inventarioRepository);
    }

    @Test
    void deberiaLanzarExcepcionCuandoGestorNoTieneInventario() {
        UUID gestorId = UUID.randomUUID();

        Gestor gestor = Gestor.builder()
                .id(gestorId)
                .nombre(Nombre.of("Farmacia X"))
                .estado(EstadoGestor.ACTIVO)
                .build();

        when(gestorRepository.findById(gestorId))
                .thenReturn(Optional.of(gestor));

        when(inventarioRepository.findByGestorId(gestorId))
                .thenReturn(Optional.empty());

        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> obtenerInventarioPorGestorUseCase.ejecutar(gestorId)
        );

        assertEquals("El gestor no tiene inventario", exception.getMessage());

        verify(gestorRepository).findById(gestorId);
        verify(inventarioRepository).findByGestorId(gestorId);
    }

    @Test
    void deberiaLanzarExcepcionCuandoGestorIdEsNulo() {
        assertThrows(BusinessRuleViolationException.class, () ->
                obtenerInventarioPorGestorUseCase.ejecutar(null)
        );

        verifyNoInteractions(gestorRepository, inventarioRepository);
    }
}