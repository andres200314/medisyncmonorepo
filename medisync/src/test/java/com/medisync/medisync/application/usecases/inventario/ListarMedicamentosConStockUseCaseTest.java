package com.medisync.medisync.application.usecases.inventario;

import com.medisync.medisync.domain.enums.EstadoGestor;
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
class ListarMedicamentosConStockUseCaseTest {

    @Mock
    private IInventarioRepository inventarioRepository;

    @InjectMocks
    private ListarMedicamentosConStockUseCase listarMedicamentosConStockUseCase;

    @Test
    void deberiaListarSoloMedicamentosConStock() {
        // ARRANGE
        UUID gestorId = UUID.randomUUID();

        Gestor gestor = Gestor.builder()
                .id(gestorId)
                .nombre(Nombre.of("Farmacia Central"))
                .estado(EstadoGestor.ACTIVO)
                .build();

        // Medicamento con stock
        Medicamento ibuprofeno = Medicamento.builder()
                .id(UUID.randomUUID())
                .nombre("Ibuprofeno")
                .requiereFormula(false)
                .build();

        // Medicamento sin stock
        Medicamento paracetamol = Medicamento.builder()
                .id(UUID.randomUUID())
                .nombre("Paracetamol")
                .requiereFormula(false)
                .build();

        // Medicamento con stock
        Medicamento amoxicilina = Medicamento.builder()
                .id(UUID.randomUUID())
                .nombre("Amoxicilina")
                .requiereFormula(true)
                .build();

        ItemInventario itemConStock1 = new ItemInventario(
                ibuprofeno,
                Cantidad.of(50),
                Precio.of(BigDecimal.valueOf(12500.00))
        );

        ItemInventario itemSinStock = new ItemInventario(
                paracetamol,
                Cantidad.of(0),
                Precio.of(BigDecimal.valueOf(8500.00))
        );

        ItemInventario itemConStock2 = new ItemInventario(
                amoxicilina,
                Cantidad.of(30),
                Precio.of(BigDecimal.valueOf(15000.00))
        );

        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestor)
                .items(List.of(itemConStock1, itemSinStock, itemConStock2))
                .build();

        when(inventarioRepository.findByGestorId(gestorId))
                .thenReturn(Optional.of(inventario));

        // ACT
        List<ItemInventario> resultado = listarMedicamentosConStockUseCase.ejecutar(gestorId);

        // ASSERT
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().anyMatch(item -> item.medicamento().getNombre().equals("Ibuprofeno")));
        assertTrue(resultado.stream().anyMatch(item -> item.medicamento().getNombre().equals("Amoxicilina")));
        assertFalse(resultado.stream().anyMatch(item -> item.medicamento().getNombre().equals("Paracetamol")));

        verify(inventarioRepository, times(1)).findByGestorId(gestorId);
    }

    @Test
    void deberiaRetornarListaVaciaCuandoNingunMedicamentoTieneStock() {
        // ARRANGE
        UUID gestorId = UUID.randomUUID();

        Gestor gestor = Gestor.builder()
                .id(gestorId)
                .nombre(Nombre.of("Farmacia Central"))
                .estado(EstadoGestor.ACTIVO)
                .build();

        Medicamento ibuprofeno = Medicamento.builder()
                .id(UUID.randomUUID())
                .nombre("Ibuprofeno")
                .requiereFormula(false)
                .build();

        Medicamento paracetamol = Medicamento.builder()
                .id(UUID.randomUUID())
                .nombre("Paracetamol")
                .requiereFormula(false)
                .build();

        ItemInventario itemSinStock1 = new ItemInventario(
                ibuprofeno,
                Cantidad.of(0),
                Precio.of(BigDecimal.valueOf(12500.00))
        );

        ItemInventario itemSinStock2 = new ItemInventario(
                paracetamol,
                Cantidad.of(0),
                Precio.of(BigDecimal.valueOf(8500.00))
        );

        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestor)
                .items(List.of(itemSinStock1, itemSinStock2))
                .build();

        when(inventarioRepository.findByGestorId(gestorId))
                .thenReturn(Optional.of(inventario));

        // ACT
        List<ItemInventario> resultado = listarMedicamentosConStockUseCase.ejecutar(gestorId);

        // ASSERT
        assertTrue(resultado.isEmpty());
        verify(inventarioRepository, times(1)).findByGestorId(gestorId);
    }

    @Test
    void deberiaRetornarListaVaciaCuandoInventarioEstaVacio() {
        // ARRANGE
        UUID gestorId = UUID.randomUUID();

        Gestor gestor = Gestor.builder()
                .id(gestorId)
                .nombre(Nombre.of("Farmacia Central"))
                .estado(EstadoGestor.ACTIVO)
                .build();

        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestor)
                .items(List.of())
                .build();

        when(inventarioRepository.findByGestorId(gestorId))
                .thenReturn(Optional.of(inventario));

        // ACT
        List<ItemInventario> resultado = listarMedicamentosConStockUseCase.ejecutar(gestorId);

        // ASSERT
        assertTrue(resultado.isEmpty());
        verify(inventarioRepository, times(1)).findByGestorId(gestorId);
    }

    @Test
    void deberiaRetornarListaVaciaCuandoGestorNoTieneInventario() {
        // ARRANGE
        UUID gestorId = UUID.randomUUID();

        when(inventarioRepository.findByGestorId(gestorId))
                .thenReturn(Optional.empty());

        // ACT
        List<ItemInventario> resultado = listarMedicamentosConStockUseCase.ejecutar(gestorId);

        // ASSERT
        assertTrue(resultado.isEmpty());
        verify(inventarioRepository, times(1)).findByGestorId(gestorId);
    }

    @Test
    void deberiaRetornarListaVaciaCuandoGestorIdEsNulo() {
        // ACT
        List<ItemInventario> resultado = listarMedicamentosConStockUseCase.ejecutar(null);

        // ASSERT
        assertTrue(resultado.isEmpty());
        verify(inventarioRepository, never()).findByGestorId(any());
    }
}