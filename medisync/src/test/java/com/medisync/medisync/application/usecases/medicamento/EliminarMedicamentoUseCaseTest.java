package com.medisync.medisync.application.usecases.medicamento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.medisync.medisync.domain.exceptions.MedicamentoNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medisync.medisync.domain.models.Medicamento;
import com.medisync.medisync.domain.repositories.IMedicamentoRepository;

import java.util.Optional;
import java.util.UUID;


@ExtendWith(MockitoExtension.class)
class EliminarMedicamentoUseCaseTest {

    @Mock
    private IMedicamentoRepository medicamentoRepository;

    @InjectMocks
    private EliminarMedicamentoUseCase eliminarMedicamentoUseCase;

    @Test
    void deberiaEliminarMedicamentoExitosamente() {
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
        eliminarMedicamentoUseCase.ejecutar(id);

        // ASSERT
        verify(medicamentoRepository, times(1)).deleteById(id);
    }

    @Test
    void deberiaLanzarExcepcionSiNoExiste() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        when(medicamentoRepository.findById(id)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(MedicamentoNotFoundException.class, () -> eliminarMedicamentoUseCase.ejecutar(id));

        verify(medicamentoRepository, never()).deleteById(id);
    }
}
