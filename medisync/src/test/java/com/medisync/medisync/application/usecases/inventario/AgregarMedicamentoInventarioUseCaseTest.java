package com.medisync.medisync.application.usecases.inventario;

import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.exceptions.MedicamentoNotFoundException;
import com.medisync.medisync.domain.models.Gestor;
import com.medisync.medisync.domain.models.Inventario;
import com.medisync.medisync.domain.models.Medicamento;
import com.medisync.medisync.domain.repositories.IGestorRepository;
import com.medisync.medisync.domain.repositories.IInventarioRepository;
import com.medisync.medisync.domain.repositories.IMedicamentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgregarMedicamentoInventarioUseCaseTest {

    @Mock
    private IInventarioRepository inventarioRepository;

    @Mock
    private IMedicamentoRepository medicamentoRepository;

    @Mock
    private IGestorRepository gestorRepository;

    @InjectMocks
    private AgregarMedicamentoInventarioUseCase useCase;

    @Test
    void deberiaAgregarNuevoMedicamento() {
        UUID gestorId = UUID.randomUUID();
        UUID medicamentoId = UUID.randomUUID();

        Gestor gestorMock = mock(Gestor.class);
        Inventario inventarioMock = mock(Inventario.class);
        Medicamento medicamentoMock = mock(Medicamento.class);

        when(gestorRepository.findById(gestorId))
                .thenReturn(Optional.of(gestorMock));

        when(inventarioRepository.findByGestorId(gestorId))
                .thenReturn(Optional.of(inventarioMock));

        when(medicamentoRepository.findById(medicamentoId))
                .thenReturn(Optional.of(medicamentoMock));

        when(inventarioRepository.save(inventarioMock))
                .thenReturn(inventarioMock);

        Inventario resultado = useCase.ejecutar(
                gestorId,
                medicamentoId,
                10,
                BigDecimal.valueOf(5000)
        );

        assertNotNull(resultado);

        verify(inventarioMock).agregarMedicamento(
                eq(medicamentoMock),
                any(),
                any()
        );

        verify(inventarioRepository).save(inventarioMock);
    }

    @Test
    void deberiaLanzarExcepcionSiGestorNoExiste() {
        UUID gestorId = UUID.randomUUID();
        UUID medicamentoId = UUID.randomUUID();

        when(gestorRepository.findById(gestorId))
                .thenReturn(Optional.empty());

        assertThrows(BusinessRuleViolationException.class, () ->
                useCase.ejecutar(gestorId, medicamentoId, 10, BigDecimal.TEN)
        );

        verify(gestorRepository).findById(gestorId);
        verifyNoInteractions(inventarioRepository, medicamentoRepository);
    }

    @Test
    void deberiaLanzarExcepcionSiInventarioNoExiste() {
        UUID gestorId = UUID.randomUUID();
        UUID medicamentoId = UUID.randomUUID();

        Gestor gestorMock = mock(Gestor.class);

        when(gestorRepository.findById(gestorId))
                .thenReturn(Optional.of(gestorMock));

        when(inventarioRepository.findByGestorId(gestorId))
                .thenReturn(Optional.empty());

        assertThrows(BusinessRuleViolationException.class, () ->
                useCase.ejecutar(gestorId, medicamentoId, 10, BigDecimal.TEN)
        );

        verify(gestorRepository).findById(gestorId);
        verify(inventarioRepository).findByGestorId(gestorId);
        verifyNoInteractions(medicamentoRepository);
    }

    @Test
    void deberiaLanzarExcepcionSiMedicamentoNoExiste() {
        UUID gestorId = UUID.randomUUID();
        UUID medicamentoId = UUID.randomUUID();

        Gestor gestorMock = mock(Gestor.class);
        Inventario inventarioMock = mock(Inventario.class);

        when(gestorRepository.findById(gestorId))
                .thenReturn(Optional.of(gestorMock));

        when(inventarioRepository.findByGestorId(gestorId))
                .thenReturn(Optional.of(inventarioMock));

        when(medicamentoRepository.findById(medicamentoId))
                .thenReturn(Optional.empty());

        assertThrows(MedicamentoNotFoundException.class, () ->
                useCase.ejecutar(gestorId, medicamentoId, 10, BigDecimal.TEN)
        );

        verify(gestorRepository).findById(gestorId);
        verify(inventarioRepository).findByGestorId(gestorId);
        verify(medicamentoRepository).findById(medicamentoId);
    }

    @Test
    void deberiaLanzarExcepcionSiIdsSonNulos() {
        assertThrows(BusinessRuleViolationException.class, () ->
                useCase.ejecutar(null, null, 10, BigDecimal.TEN)
        );

        verifyNoInteractions(gestorRepository, inventarioRepository, medicamentoRepository);
    }
}