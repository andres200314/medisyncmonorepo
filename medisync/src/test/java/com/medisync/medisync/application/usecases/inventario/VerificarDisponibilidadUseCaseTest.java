package com.medisync.medisync.application.usecases.inventario;

import com.medisync.medisync.domain.enums.EstadoGestor;
import com.medisync.medisync.domain.models.Gestor;
import com.medisync.medisync.domain.models.Inventario;
import com.medisync.medisync.domain.models.Medicamento;
import com.medisync.medisync.domain.repositories.IInventarioRepository;
import com.medisync.medisync.domain.repositories.IMedicamentoRepository;
import com.medisync.medisync.domain.valueobjects.*;
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
class BuscarDisponibilidadMedicamentoUseCaseTest {

    @Mock
    private IInventarioRepository inventarioRepository;

    @Mock
    private IMedicamentoRepository medicamentoRepository;

    @InjectMocks
    private BuscarDisponibilidadMedicamentoUseCase useCase;

    @Test
    void deberiaRetornarInventariosConMedicamentoDisponible() {
        String nombre = "Ibu";
        UUID medicamentoId = UUID.randomUUID();

        Medicamento medicamento = Medicamento.builder()
                .id(medicamentoId)
                .nombre("Ibuprofeno")
                .requiereFormula(false)
                .build();

        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(Nombre.of("Farmacia"))
                .estado(EstadoGestor.ACTIVO)
                .coordenadas(Coordenadas.of(6.24, -75.57))
                .build();

        ItemInventario item = new ItemInventario(
                medicamento,
                Cantidad.of(10),
                Precio.of(BigDecimal.valueOf(1000))
        );

        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestor)
                .items(List.of(item))
                .build();

        when(medicamentoRepository.findByNombreContainingIgnoreCase(nombre))
                .thenReturn(List.of(medicamento));

        when(inventarioRepository.findAll())
                .thenReturn(List.of(inventario));

        List<Inventario> resultado = useCase.ejecutar(nombre);

        assertEquals(1, resultado.size());
        verify(medicamentoRepository).findByNombreContainingIgnoreCase(nombre);
        verify(inventarioRepository).findAll();
    }

    @Test
    void deberiaRetornarListaVaciaSiNoHayCoincidencias() {
        String nombre = "Paracetamol";

        when(medicamentoRepository.findByNombreContainingIgnoreCase(nombre))
                .thenReturn(List.of());

        when(inventarioRepository.findAll())
                .thenReturn(List.of());

        List<Inventario> resultado = useCase.ejecutar(nombre);

        assertTrue(resultado.isEmpty());
        verify(medicamentoRepository).findByNombreContainingIgnoreCase(nombre);
        verify(inventarioRepository).findAll();
    }

    @Test
    void deberiaIgnorarInventariosSinStock() {
        String nombre = "Ibu";
        UUID medicamentoId = UUID.randomUUID();

        Medicamento medicamento = Medicamento.builder()
                .id(medicamentoId)
                .nombre("Ibuprofeno")
                .requiereFormula(false)
                .build();

        Gestor gestor = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(Nombre.of("Farmacia"))
                .estado(EstadoGestor.ACTIVO)
                .build();

        ItemInventario itemSinStock = new ItemInventario(
                medicamento,
                Cantidad.of(0),
                Precio.of(BigDecimal.valueOf(1000))
        );

        Inventario inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestor)
                .items(List.of(itemSinStock))
                .build();

        when(medicamentoRepository.findByNombreContainingIgnoreCase(nombre))
                .thenReturn(List.of(medicamento));

        when(inventarioRepository.findAll())
                .thenReturn(List.of(inventario));

        List<Inventario> resultado = useCase.ejecutar(nombre);

        assertTrue(resultado.isEmpty());
        verify(medicamentoRepository).findByNombreContainingIgnoreCase(nombre);
        verify(inventarioRepository).findAll();
    }
}