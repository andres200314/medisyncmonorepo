package com.medisync.medisync.application.usecases.medicamento;

import com.medisync.medisync.application.usecases.inventario.AgregarMedicamentoInventarioUseCase;
import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.models.Medicamento;
import com.medisync.medisync.domain.repositories.IMedicamentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgregarOActualizarMedicamentoInventarioUseCaseTest {

    @Mock
    private IMedicamentoRepository medicamentoRepository;

    @Mock
    private CrearMedicamentoUseCase crearMedicamentoUseCase;

    @Mock
    private AgregarMedicamentoInventarioUseCase agregarMedicamentoInventarioUseCase;

    @InjectMocks
    private AgregarOActualizarMedicamentoInventarioUseCase useCase;

    @Test
    void cuandoHayCoincidenciaExactaPorNombre_noCreaMedicamento() {
        UUID gestorId = UUID.randomUUID();
        UUID medicamentoId = UUID.randomUUID();

        Medicamento existente = Medicamento.builder()
                .id(medicamentoId)
                .nombre("Ibuprofeno")
                .requiereFormula(false)
                .descripcion("En catálogo")
                .build();

        when(medicamentoRepository.findByNombreContainingIgnoreCase("Ibuprofeno"))
                .thenReturn(List.of(existente));

        Medicamento resultado = useCase.ejecutar(
                gestorId,
                "IBUPROFENO",
                true,
                "Otra descripción",
                3,
                BigDecimal.valueOf(2500)
        );

        assertSame(existente, resultado);
        verifyNoInteractions(crearMedicamentoUseCase);
        verify(agregarMedicamentoInventarioUseCase).ejecutar(
                gestorId,
                medicamentoId,
                3,
                BigDecimal.valueOf(2500)
        );
    }

    @Test
    void cuandoSoloHayCoincidenciasParciales_creaMedicamentoYLuegoAgregaAlInventario() {
        UUID gestorId = UUID.randomUUID();
        UUID nuevoId = UUID.randomUUID();

        Medicamento otro = Medicamento.builder()
                .id(UUID.randomUUID())
                .nombre("Ibuprofeno forte")
                .requiereFormula(false)
                .descripcion("Combo")
                .build();

        Medicamento creado = Medicamento.builder()
                .id(nuevoId)
                .nombre("Ibuprofeno")
                .requiereFormula(false)
                .descripcion("Genérico")
                .build();

        when(medicamentoRepository.findByNombreContainingIgnoreCase("Ibuprofeno"))
                .thenReturn(List.of(otro));

        when(crearMedicamentoUseCase.ejecutar("Ibuprofeno", false, "Genérico"))
                .thenReturn(creado);

        Medicamento resultado = useCase.ejecutar(
                gestorId,
                "Ibuprofeno",
                false,
                "Genérico",
                10,
                BigDecimal.valueOf(5000)
        );

        assertEquals(nuevoId, resultado.getId());
        verify(crearMedicamentoUseCase).ejecutar("Ibuprofeno", false, "Genérico");
        verify(agregarMedicamentoInventarioUseCase).ejecutar(
                gestorId,
                nuevoId,
                10,
                BigDecimal.valueOf(5000)
        );
    }

    @Test
    void cuandoNoHayCandidatos_creaMedicamentoYLuegoAgregaAlInventario() {
        UUID gestorId = UUID.randomUUID();
        UUID nuevoId = UUID.randomUUID();

        Medicamento creado = Medicamento.builder()
                .id(nuevoId)
                .nombre("Paracetamol")
                .requiereFormula(false)
                .descripcion("Analgésico común para uso frecuente")
                .build();

        when(medicamentoRepository.findByNombreContainingIgnoreCase("Paracetamol"))
                .thenReturn(List.of());

        when(crearMedicamentoUseCase.ejecutar("Paracetamol", false, "Analgésico común para uso frecuente"))
                .thenReturn(creado);

        Medicamento resultado = useCase.ejecutar(
                gestorId,
                "Paracetamol",
                false,
                "Analgésico común para uso frecuente",
                7,
                BigDecimal.valueOf(1200)
        );

        assertEquals(nuevoId, resultado.getId());
        verify(agregarMedicamentoInventarioUseCase).ejecutar(
                gestorId,
                nuevoId,
                7,
                BigDecimal.valueOf(1200)
        );
    }

    @Test
    void deberiaLanzarSiCantidadInicialEsNula() {
        assertThrows(BusinessRuleViolationException.class, () ->
                useCase.ejecutar(
                        UUID.randomUUID(),
                        "Abc test largo",
                        false,
                        "Descripción suficientemente larga para validar",
                        null,
                        BigDecimal.ONE
                ));
    }
}
