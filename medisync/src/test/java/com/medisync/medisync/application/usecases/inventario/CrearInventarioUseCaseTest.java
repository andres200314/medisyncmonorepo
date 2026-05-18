package com.medisync.medisync.application.usecases.inventario;

import com.medisync.medisync.domain.models.Gestor;
import com.medisync.medisync.domain.models.Inventario;
import com.medisync.medisync.domain.repositories.IInventarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrearInventarioUseCaseTest {

    @Mock
    private IInventarioRepository inventarioRepository;

    private CrearInventarioUseCase crearInventarioUseCase;

    private UUID gestorId;
    private Gestor gestor;
    private Inventario inventario;

    @BeforeEach
    void setUp() {
        crearInventarioUseCase = new CrearInventarioUseCase(inventarioRepository);
        gestorId = UUID.randomUUID();

        gestor = Gestor.builder()
                .id(gestorId)
                .build();

        inventario = Inventario.builder()
                .id(UUID.randomUUID())
                .gestor(gestor)
                .build();
    }

    @Test
    void deberiaCrearInventarioExitosamente() {
        // Given
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        // When
        Inventario resultado = crearInventarioUseCase.ejecutar(inventario);

        // Then
        assertNotNull(resultado);
        verify(inventarioRepository, times(1)).save(any(Inventario.class));
    }

    @Test
    void deberiaGuardarInventarioCorrectamente() {
        // Given
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(invocation -> {
            Inventario inv = invocation.getArgument(0);
            return Inventario.builder()
                    .id(UUID.randomUUID())
                    .gestor(inv.getGestor())
                    .items(inv.getItems())
                    .build();
        });

        // When
        Inventario resultado = crearInventarioUseCase.ejecutar(inventario);

        // Then
        assertNotNull(resultado.getId());
        assertEquals(gestorId, resultado.getGestor().getId());
        verify(inventarioRepository, times(1)).save(inventario);
    }

    @Test
    void deberiaLanzarExcepcionSiRepositoryFalla() {
        // Given
        when(inventarioRepository.save(any(Inventario.class))).thenThrow(new RuntimeException("Error de base de datos"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            crearInventarioUseCase.ejecutar(inventario);
        });

        verify(inventarioRepository, times(1)).save(any(Inventario.class));
    }
}