package com.medisync.medisync.application.usecases.medicamento;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medisync.medisync.domain.exceptions.MedicamentoNotFoundException;
import com.medisync.medisync.domain.models.Medicamento;
import com.medisync.medisync.domain.repositories.IMedicamentoRepository;

@ExtendWith(MockitoExtension.class)
class ActualizarMedicamentoUseCaseTest {

    @Mock
    private IMedicamentoRepository medicamentoRepository;

    @InjectMocks
    private ActualizarMedicamentoUseCase actualizarMedicamentoUseCase;

    private static final String NOMBRE = "Paracetamol";
    private static final String DESCRIPCION = "Analgésico y antipirético";

    @Test
    void deberiaActualizarMedicamentoExitosamente() {
        UUID id = UUID.randomUUID();
        Medicamento existente = Medicamento.builder()
                .id(id)
                .nombre("Ibuprofeno")
                .requiereFormula(false)
                .descripcion("Antiinflamatorio")
                .build();

        when(medicamentoRepository.findById(id)).thenReturn(Optional.of(existente));
        when(medicamentoRepository.save(any(Medicamento.class))).thenAnswer(i -> i.getArgument(0));

        Medicamento resultado = actualizarMedicamentoUseCase.ejecutar(id, NOMBRE, DESCRIPCION);

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("Paracetamol", resultado.getNombre());
        assertEquals(DESCRIPCION, resultado.getDescripcion());
        verify(medicamentoRepository, times(1)).findById(id);
        verify(medicamentoRepository, times(1)).save(any(Medicamento.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoMedicamentoNoExiste() {
        UUID id = UUID.randomUUID();
        when(medicamentoRepository.findById(id)).thenReturn(Optional.empty());

        MedicamentoNotFoundException ex = assertThrows(
                MedicamentoNotFoundException.class,
                () -> actualizarMedicamentoUseCase.ejecutar(id, NOMBRE, DESCRIPCION)
        );

        assertTrue(ex.getMessage().contains(id.toString()));
        verify(medicamentoRepository, times(1)).findById(id);
        verify(medicamentoRepository, never()).save(any(Medicamento.class));
    }

    @Test
    void deberiaMantenerElMismoIdAlActualizar() {
        UUID id = UUID.randomUUID();
        Medicamento existente = Medicamento.builder()
                .id(id)
                .nombre("Ibuprofeno")
                .requiereFormula(false)
                .descripcion("Antiinflamatorio")
                .build();

        when(medicamentoRepository.findById(id)).thenReturn(Optional.of(existente));
        when(medicamentoRepository.save(any(Medicamento.class))).thenAnswer(i -> i.getArgument(0));

        Medicamento resultado = actualizarMedicamentoUseCase.ejecutar(id, "Amoxicilina", "Antibiótico de amplio espectro");

        assertEquals(id, resultado.getId());
        assertEquals("Amoxicilina", resultado.getNombre());
    }
}
