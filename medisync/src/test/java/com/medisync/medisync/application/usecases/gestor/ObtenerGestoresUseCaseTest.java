package com.medisync.medisync.application.usecases.gestor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medisync.medisync.domain.enums.EstadoGestor;
import com.medisync.medisync.domain.models.Gestor;
import com.medisync.medisync.domain.repositories.IGestorRepository;
import com.medisync.medisync.domain.valueobjects.Coordenadas;
import com.medisync.medisync.domain.valueobjects.Email;
import com.medisync.medisync.domain.valueobjects.Nit;
import com.medisync.medisync.domain.valueobjects.Nombre;
import com.medisync.medisync.domain.valueobjects.Telefono;

@ExtendWith(MockitoExtension.class)
class ObtenerGestoresUseCaseTest {

    @Mock
    private IGestorRepository gestorRepository;

    @InjectMocks
    private ObtenerGestoresUseCase obtenerGestoresUseCase;

    @Test
    void deberiaRetornarListaDeGestores() {
        // ARRANGE
        List<Gestor> gestores = List.of(
                Gestor.builder()
                        .id(UUID.randomUUID())
                        .nombre(new Nombre("Farmacia Central"))
                        .nit(new Nit("900123456-1"))
                        .direccion("Calle 10 #20-30")
                        .telefono(new Telefono("3001234567"))
                        .email(new Email("farmacia@central.com"))
                        .passwordHash("$2a$10$hasheado1")
                        .coordenadas(new Coordenadas(new BigDecimal("6.2442"), new BigDecimal("-75.5812")))
                        .estado(EstadoGestor.ACTIVO)
                        .build(),
                Gestor.builder()
                        .id(UUID.randomUUID())
                        .nombre(new Nombre("Droguería San José"))
                        .nit(new Nit("800987654-2"))
                        .direccion("Carrera 5 #15-20")
                        .telefono(new Telefono("3109876543"))
                        .email(new Email("drogueria@sanjose.com"))
                        .passwordHash("$2a$10$hasheado2")
                        .coordenadas(new Coordenadas(new BigDecimal("6.2530"), new BigDecimal("-75.5743")))
                        .estado(EstadoGestor.ACTIVO)
                        .build()
        );

        when(gestorRepository.findAll()).thenReturn(gestores);

        // ACT
        List<Gestor> resultado = obtenerGestoresUseCase.ejecutar();

        // ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Farmacia Central", resultado.get(0).getNombre().valor());
        assertEquals("Droguería San José", resultado.get(1).getNombre().valor());
        verify(gestorRepository, times(1)).findAll();
    }

    @Test
    void deberiaRetornarListaVaciaSiNoHayGestores() {
        // ARRANGE
        when(gestorRepository.findAll()).thenReturn(List.of());

        // ACT
        List<Gestor> resultado = obtenerGestoresUseCase.ejecutar();

        // ASSERT
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(gestorRepository, times(1)).findAll();
    }

    @Test
    void deberiaMantenerPropiedadesDeCadaGestor() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        Gestor gestor = Gestor.builder()
                .id(id)
                .nombre(new Nombre("Farmacia Central"))
                .nit(new Nit("900123456-1"))
                .direccion("Calle 10 #20-30")
                .telefono(new Telefono("3001234567"))
                .email(new Email("farmacia@central.com"))
                .passwordHash("$2a$10$hasheado1")
                .coordenadas(new Coordenadas(new BigDecimal("6.2442"), new BigDecimal("-75.5812")))
                .estado(EstadoGestor.ACTIVO)
                .build();

        when(gestorRepository.findAll()).thenReturn(List.of(gestor));

        // ACT
        List<Gestor> resultado = obtenerGestoresUseCase.ejecutar();

        // ASSERT
        assertEquals(id, resultado.getFirst().getId());
        assertEquals("Farmacia Central", resultado.getFirst().getNombre().valor());
        assertEquals("900123456-1", resultado.getFirst().getNit().valor());
        assertEquals("farmacia@central.com", resultado.getFirst().getEmail().valor());
        assertEquals(new BigDecimal("6.2442"), resultado.getFirst().getCoordenadas().latitud());
        assertEquals(new BigDecimal("-75.5812"), resultado.getFirst().getCoordenadas().longitud());
    }
}