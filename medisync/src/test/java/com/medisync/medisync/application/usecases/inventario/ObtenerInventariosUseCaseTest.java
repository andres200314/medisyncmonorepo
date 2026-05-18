package com.medisync.medisync.application.usecases.inventario;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medisync.medisync.domain.enums.EstadoGestor;
import com.medisync.medisync.domain.models.Gestor;
import com.medisync.medisync.domain.models.Inventario;
import com.medisync.medisync.domain.models.Medicamento;
import com.medisync.medisync.domain.repositories.IInventarioRepository;
import com.medisync.medisync.domain.valueobjects.*;

@ExtendWith(MockitoExtension.class)
class ObtenerInventariosUseCaseTest {

    @Mock
    private IInventarioRepository inventarioRepository;

    @InjectMocks
    private ObtenerInventariosUseCase obtenerInventariosUseCase;

    @Test
    void deberiaRetornarListaDeInventarios() {
        // ARRANGE
        UUID gestorId1 = UUID.randomUUID();
        UUID gestorId2 = UUID.randomUUID();

        // Crear Value Objects
        Nombre nombreGestor1 = Nombre.of("Farmacia Central");
        Nombre nombreGestor2 = Nombre.of("Droguería San José");

        // Crear gestores con estado ACTIVO
        Gestor gestor1 = Gestor.builder()
                .id(gestorId1)
                .nombre(nombreGestor1)
                .estado(EstadoGestor.ACTIVO)
                .build();

        Gestor gestor2 = Gestor.builder()
                .id(gestorId2)
                .nombre(nombreGestor2)
                .estado(EstadoGestor.ACTIVO)
                .build();

        // Crear medicamentos
        Medicamento ibuprofeno = Medicamento.builder()
                .id(UUID.randomUUID())
                .nombre("Ibuprofeno")
                .requiereFormula(false)
                .build();

        Medicamento amoxicilina = Medicamento.builder()
                .id(UUID.randomUUID())
                .nombre("Amoxicilina")
                .requiereFormula(true)
                .build();

        // Crear items de inventario
        ItemInventario item1 = new ItemInventario(
                ibuprofeno,
                Cantidad.of(50),
                Precio.of(BigDecimal.valueOf(12500.00))
        );

        ItemInventario item2 = new ItemInventario(
                amoxicilina,
                Cantidad.of(30),
                Precio.of(BigDecimal.valueOf(8500.00))
        );

        // Crear inventarios
        Inventario inventario1 = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestor1)
                .items(List.of(item1))
                .build();

        Inventario inventario2 = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestor2)
                .items(List.of(item2))
                .build();

        List<Inventario> inventarios = List.of(inventario1, inventario2);

        when(inventarioRepository.findAll()).thenReturn(inventarios);

        // ACT
        List<Inventario> resultado = obtenerInventariosUseCase.ejecutar();

        // ASSERT
        assertEquals(2, resultado.size());

        // Verificar primer inventario
        Inventario primero = resultado.getFirst();
        assertEquals(gestorId1, primero.getGestor().getId());
        assertEquals("Farmacia Central", primero.getGestor().getNombre().valor());
        assertEquals(1, primero.getItems().size());
        assertEquals("Ibuprofeno", primero.getItems().getFirst().medicamento().getNombre());
        assertEquals(50, primero.getItems().getFirst().cantidad().valor());

        // ✅ Usar compareTo para BigDecimal (ignora escala)
        assertEquals(0, BigDecimal.valueOf(12500.00)
                .compareTo(primero.getItems().getFirst().precioUnitario().valor()));

        // Verificar segundo inventario
        Inventario segundo = resultado.get(1);
        assertEquals(gestorId2, segundo.getGestor().getId());
        assertEquals("Droguería San José", segundo.getGestor().getNombre().valor());
        assertEquals(1, segundo.getItems().size());
        assertEquals("Amoxicilina", segundo.getItems().getFirst().medicamento().getNombre());
        assertEquals(30, segundo.getItems().getFirst().cantidad().valor());

        // ✅ Usar compareTo para BigDecimal (ignora escala)
        assertEquals(0, BigDecimal.valueOf(8500.00)
                .compareTo(segundo.getItems().getFirst().precioUnitario().valor()));

        verify(inventarioRepository, times(1)).findAll();
    }

    @Test
    void deberiaRetornarListaVaciaSiNoHayInventarios() {
        // ARRANGE
        when(inventarioRepository.findAll()).thenReturn(List.of());

        // ACT
        List<Inventario> resultado = obtenerInventariosUseCase.ejecutar();

        // ASSERT
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(inventarioRepository, times(1)).findAll();
    }

    @Test
    void deberiaMantenerPropiedadesDeCadaInventario() {
        // ARRANGE
        UUID inventarioId = UUID.randomUUID();
        UUID medicamentoId = UUID.randomUUID();
        UUID gestorId = UUID.randomUUID();

        // Crear Value Objects
        Nombre nombreGestor = Nombre.of("Farmacia Central");

        // Crear gestor con estado ACTIVO
        Gestor gestor = Gestor.builder()
                .id(gestorId)
                .nombre(nombreGestor)
                .estado(EstadoGestor.ACTIVO)
                .build();

        // Crear medicamento
        Medicamento ibuprofeno = Medicamento.builder()
                .id(medicamentoId)
                .nombre("Ibuprofeno")
                .requiereFormula(false)
                .build();

        // Crear item de inventario
        ItemInventario item = new ItemInventario(
                ibuprofeno,
                Cantidad.of(50),
                Precio.of(BigDecimal.valueOf(12500.00))
        );

        // Crear inventario
        Inventario inventario = Inventario.builder()
                .id(inventarioId)
                .gestor(gestor)
                .items(List.of(item))
                .build();

        when(inventarioRepository.findAll()).thenReturn(List.of(inventario));

        // ACT
        List<Inventario> resultado = obtenerInventariosUseCase.ejecutar();

        // ASSERT
        Inventario resultadoInventario = resultado.getFirst();
        assertEquals(inventarioId, resultadoInventario.getId());
        assertEquals(gestorId, resultadoInventario.getGestor().getId());
        assertEquals("Farmacia Central", resultadoInventario.getGestor().getNombre().valor());

        // Verificar item
        assertEquals(1, resultadoInventario.getItems().size());
        assertEquals(medicamentoId, resultadoInventario.getItems().getFirst().medicamento().getId());
        assertEquals("Ibuprofeno", resultadoInventario.getItems().getFirst().medicamento().getNombre());
        assertEquals(50, resultadoInventario.getItems().getFirst().cantidad().valor());

        // ✅ Usar compareTo para BigDecimal (ignora escala)
        assertEquals(0, BigDecimal.valueOf(12500.00)
                .compareTo(resultadoInventario.getItems().getFirst().precioUnitario().valor()));

        verify(inventarioRepository, times(1)).findAll();
    }
}