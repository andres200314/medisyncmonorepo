package com.medisync.medisync.application.usecases.inventario;

import com.medisync.medisync.domain.enums.EstadoGestor;
import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.exceptions.MedicamentoNotFoundException;
import com.medisync.medisync.domain.models.Gestor;
import com.medisync.medisync.domain.models.Inventario;
import com.medisync.medisync.domain.models.Medicamento;
import com.medisync.medisync.domain.repositories.IInventarioRepository;
import com.medisync.medisync.domain.repositories.IMedicamentoRepository;
import com.medisync.medisync.domain.valueobjects.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AjustarStockUseCaseTest {

    @Mock
    private IInventarioRepository inventarioRepository;

    @Mock
    private IMedicamentoRepository medicamentoRepository;

    private AjustarStockUseCase useCase;

    private static final UUID GESTOR_ID = UUID.randomUUID();
    private static final UUID MEDICAMENTO_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new AjustarStockUseCase(inventarioRepository, medicamentoRepository);
    }

    private Gestor gestorActivo() {
        return Gestor.builder()
                .id(GESTOR_ID)
                .nombre(Nombre.of("Farmacia"))
                .estado(EstadoGestor.ACTIVO)
                .build();
    }

    private Medicamento medicamento() {
        return Medicamento.builder()
                .id(MEDICAMENTO_ID)
                .nombre("Ibuprofeno")
                .build();
    }

    @Test
    void deberiaReducirStock() {
        Medicamento medicamento = medicamento();
        ItemInventario item = new ItemInventario(
                medicamento,
                Cantidad.of(20),
                Precio.of(BigDecimal.valueOf(5000))
        );

        Inventario inventario = Inventario.builder()
                .gestor(gestorActivo())
                .items(new ArrayList<>(List.of(item)))
                .build();

        when(inventarioRepository.findByGestorId(GESTOR_ID)).thenReturn(Optional.of(inventario));
        when(medicamentoRepository.findById(MEDICAMENTO_ID)).thenReturn(Optional.of(medicamento));
        when(inventarioRepository.save(inventario)).thenReturn(inventario);

        Inventario resultado = useCase.ejecutar(GESTOR_ID, MEDICAMENTO_ID, -5);

        assertEquals(15, resultado.getItems().getFirst().cantidad().valor());
        verify(inventarioRepository).save(inventario);
    }

    @Test
    void deberiaAumentarStock() {
        Medicamento medicamento = medicamento();
        ItemInventario item = new ItemInventario(
                medicamento,
                Cantidad.of(10),
                Precio.of(BigDecimal.valueOf(5000))
        );

        Inventario inventario = Inventario.builder()
                .gestor(gestorActivo())
                .items(new ArrayList<>(List.of(item)))
                .build();

        when(inventarioRepository.findByGestorId(GESTOR_ID)).thenReturn(Optional.of(inventario));
        when(medicamentoRepository.findById(MEDICAMENTO_ID)).thenReturn(Optional.of(medicamento));
        when(inventarioRepository.save(inventario)).thenReturn(inventario);

        Inventario resultado = useCase.ejecutar(GESTOR_ID, MEDICAMENTO_ID, 5);

        assertEquals(15, resultado.getItems().getFirst().cantidad().valor());
        verify(inventarioRepository).save(inventario);
    }

    @Test
    void deberiaFallarPorStockInsuficiente() {
        Medicamento medicamento = medicamento();
        ItemInventario item = new ItemInventario(
                medicamento,
                Cantidad.of(5),
                Precio.of(BigDecimal.valueOf(5000))
        );

        Inventario inventario = Inventario.builder()
                .gestor(gestorActivo())
                .items(new ArrayList<>(List.of(item)))
                .build();

        when(inventarioRepository.findByGestorId(GESTOR_ID)).thenReturn(Optional.of(inventario));
        when(medicamentoRepository.findById(MEDICAMENTO_ID)).thenReturn(Optional.of(medicamento));

        assertThrows(BusinessRuleViolationException.class, () ->
                useCase.ejecutar(GESTOR_ID, MEDICAMENTO_ID, -10)
        );
    }

    @Test
    void deberiaFallarSiInventarioNoExiste() {
        when(inventarioRepository.findByGestorId(GESTOR_ID)).thenReturn(Optional.empty());

        assertThrows(BusinessRuleViolationException.class, () ->
                useCase.ejecutar(GESTOR_ID, MEDICAMENTO_ID, -5)
        );
    }

    @Test
    void deberiaFallarSiMedicamentoNoExiste() {
        Inventario inventario = Inventario.builder()
                .gestor(gestorActivo())
                .items(new ArrayList<>())
                .build();

        when(inventarioRepository.findByGestorId(GESTOR_ID)).thenReturn(Optional.of(inventario));
        when(medicamentoRepository.findById(MEDICAMENTO_ID)).thenReturn(Optional.empty());

        assertThrows(MedicamentoNotFoundException.class, () ->
                useCase.ejecutar(GESTOR_ID, MEDICAMENTO_ID, -5)
        );
    }
}