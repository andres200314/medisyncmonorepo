package com.medisync.medisync.application.usecases.medicamento;

import com.medisync.medisync.domain.models.Medicamento;
import com.medisync.medisync.domain.repositories.IMedicamentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObtenerMedicamentoPorNombreUseCaseTest {

    @Mock
    private IMedicamentoRepository medicamentoRepository;

    @InjectMocks
    private ObtenerMedicamentoPorNombreUseCase obtenerMedicamentoPorNombreUseCase;

    @Test
    void deberiaRetornarMedicamentosQueCoincidanConElNombre() {
        // ARRANGE
        Medicamento ibuprofeno = Medicamento.builder()
                .id(UUID.randomUUID())
                .nombre("Ibuprofeno")
                .requiereFormula(false)
                .descripcion("Antiinflamatorio")
                .build();

        Medicamento ibuprofenoInfantil = Medicamento.builder()
                .id(UUID.randomUUID())
                .nombre("Ibuprofeno Infantil")
                .requiereFormula(true)
                .descripcion("Ibuprofeno para niños")
                .build();

        when(medicamentoRepository.findByNombreContainingIgnoreCase("ibuprofeno"))
                .thenReturn(List.of(ibuprofeno, ibuprofenoInfantil));

        // ACT
        List<Medicamento> resultado = obtenerMedicamentoPorNombreUseCase.ejecutar("ibuprofeno");

        // ASSERT
        assertEquals(2, resultado.size());
        verify(medicamentoRepository, times(1))
                .findByNombreContainingIgnoreCase("ibuprofeno");
    }

    @Test
    void deberiaRetornarListaVaciaSiNoHayCoincidencias() {
        // ARRANGE
        when(medicamentoRepository.findByNombreContainingIgnoreCase("paracetamol"))
                .thenReturn(List.of());

        // ACT
        List<Medicamento> resultado = obtenerMedicamentoPorNombreUseCase.ejecutar("paracetamol");

        // ASSERT
        assertTrue(resultado.isEmpty());
        verify(medicamentoRepository, times(1))
                .findByNombreContainingIgnoreCase("paracetamol");
    }

    @Test
    void deberiaRetornarListaVaciaSiNombreEsNulo() {
        // ACT
        List<Medicamento> resultado = obtenerMedicamentoPorNombreUseCase.ejecutar(null);

        // ASSERT
        assertTrue(resultado.isEmpty());
        verify(medicamentoRepository, never()).findByNombreContainingIgnoreCase(any());
    }

    @Test
    void deberiaRetornarListaVaciaSiNombreEstaVacio() {
        // ACT
        List<Medicamento> resultado = obtenerMedicamentoPorNombreUseCase.ejecutar("");

        // ASSERT
        assertTrue(resultado.isEmpty());
        verify(medicamentoRepository, never()).findByNombreContainingIgnoreCase(any());
    }

    @Test
    void deberiaIgnorarMayusculasYMinusculas() {
        // ARRANGE
        Medicamento ibuprofeno = Medicamento.builder()
                .id(UUID.randomUUID())
                .nombre("Ibuprofeno")
                .requiereFormula(false)
                .descripcion("Antiinflamatorio")
                .build();

        when(medicamentoRepository.findByNombreContainingIgnoreCase("IBUPROFENO"))
                .thenReturn(List.of(ibuprofeno));

        // ACT
        List<Medicamento> resultado = obtenerMedicamentoPorNombreUseCase.ejecutar("IBUPROFENO");

        // ASSERT
        assertEquals(1, resultado.size());
        verify(medicamentoRepository, times(1))
                .findByNombreContainingIgnoreCase("IBUPROFENO");
    }
}