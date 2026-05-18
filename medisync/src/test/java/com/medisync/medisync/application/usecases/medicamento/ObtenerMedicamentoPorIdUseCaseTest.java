package com.medisync.medisync.application.usecases.medicamento;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medisync.medisync.domain.exceptions.MedicamentoNotFoundException;
import com.medisync.medisync.domain.models.Medicamento;
import com.medisync.medisync.domain.repositories.IMedicamentoRepository;

@ExtendWith(MockitoExtension.class)
class ObtenerMedicamentoPorIdUseCaseTest {

    @Mock
    private IMedicamentoRepository medicamentoRepository;

    @InjectMocks
    private ObtenerMedicamentoPorIdUseCase obtenerMedicamentoPorIdUseCase;

    @Test
    void deberiaRetornarMedicamentoCuandoExiste() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        Medicamento medicamento = Medicamento.builder()
                .id(id)
                .nombre("Ibuprofeno")
                .requiereFormula(false)
                .descripcion("Antiinflamatorio y analgésico")
                .build();

        when(medicamentoRepository.findById(id)).thenReturn(Optional.of(medicamento));

        // ACT
        Medicamento resultado = obtenerMedicamentoPorIdUseCase.ejecutar(id);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("Ibuprofeno", resultado.getNombre());
        assertFalse(resultado.getRequiereFormula());
        assertEquals("Antiinflamatorio y analgésico", resultado.getDescripcion());
        verify(medicamentoRepository, times(1)).findById(id);
    }

    @Test
    void deberiaLanzarExcepcionCuandoNoExiste() {
        // ARRANGE
        UUID id = UUID.randomUUID();

        when(medicamentoRepository.findById(id)).thenReturn(Optional.empty());

        // ACT & ASSERT
        MedicamentoNotFoundException ex = assertThrows(
                MedicamentoNotFoundException.class,
                () -> obtenerMedicamentoPorIdUseCase.ejecutar(id)
        );

        assertEquals("Medicamento no encontrado con id: " + id, ex.getMessage());
        verify(medicamentoRepository, times(1)).findById(id);
    }

    @Test
    void deberiaMantenerTodasLasPropiedades() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        Medicamento medicamento = Medicamento.builder()
                .id(id)
                .nombre("Amoxicilina")
                .requiereFormula(true)
                .descripcion("Antibiótico")
                .build();

        when(medicamentoRepository.findById(id)).thenReturn(Optional.of(medicamento));

        // ACT
        Medicamento resultado = obtenerMedicamentoPorIdUseCase.ejecutar(id);

        // ASSERT
        assertEquals(id, resultado.getId());
        assertEquals("Amoxicilina", resultado.getNombre());
        assertTrue(resultado.getRequiereFormula());
        assertEquals("Antibiótico", resultado.getDescripcion());
    }
}