package com.medisync.medisync.application.usecases.medicamento;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.models.Medicamento;
import com.medisync.medisync.domain.repositories.IMedicamentoRepository;

@ExtendWith(MockitoExtension.class)
class CrearMedicamentoUseCaseTest {

    @Mock
    private IMedicamentoRepository medicamentoRepository;

    @InjectMocks
    private CrearMedicamentoUseCase crearMedicamentoUseCase;

    @Test
    void deberiaCrearMedicamentoExitosamente() {
        when(medicamentoRepository.save(any(Medicamento.class))).thenAnswer(i -> {
            Medicamento m = i.getArgument(0);
            return Medicamento.builder()
                    .id(UUID.randomUUID())
                    .nombre(m.getNombre())
                    .requiereFormula(m.getRequiereFormula())
                    .descripcion(m.getDescripcion())
                    .build();
        });

        Medicamento resultado = crearMedicamentoUseCase.ejecutar("Ibuprofeno", false, "Antiinflamatorio y analgésico");

        assertNotNull(resultado.getId());
        assertEquals("Ibuprofeno", resultado.getNombre());
        assertFalse(resultado.getRequiereFormula());
        verify(medicamentoRepository, times(1)).save(any(Medicamento.class));
    }

    @Test
    void deberiaMantenerPropiedadesCorrectamente() {
        when(medicamentoRepository.save(any(Medicamento.class))).thenAnswer(i -> i.getArgument(0));

        Medicamento resultado = crearMedicamentoUseCase.ejecutar(
                "Amoxicilina", true, "Antibiótico de amplio espectro para infecciones bacterianas");

        assertEquals("Amoxicilina", resultado.getNombre());
        assertTrue(resultado.getRequiereFormula());
        assertEquals("Antibiótico de amplio espectro para infecciones bacterianas", resultado.getDescripcion());
    }

    @Test
    void deberiaLanzarExcepcionSiNombreEsVacio() {
        assertThrows(BusinessRuleViolationException.class,
                () -> crearMedicamentoUseCase.ejecutar("", false, "Antiinflamatorio"));
        verify(medicamentoRepository, never()).save(any());
    }

    @Test
    void deberiaLanzarExcepcionSiDescripcionEsInsuficienteConFormula() {
        assertThrows(BusinessRuleViolationException.class,
                () -> crearMedicamentoUseCase.ejecutar("Amoxicilina", true, "Antibiótico"));
        verify(medicamentoRepository, never()).save(any());
    }
}