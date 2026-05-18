package com.medisync.medisync.application.usecases.gestor;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medisync.medisync.domain.exceptions.GestorNotFoundException;
import com.medisync.medisync.domain.models.Gestor;
import com.medisync.medisync.domain.repositories.IGestorRepository;
import com.medisync.medisync.domain.valueobjects.Coordenadas;
import com.medisync.medisync.domain.valueobjects.Email;
import com.medisync.medisync.domain.valueobjects.Nit;
import com.medisync.medisync.domain.valueobjects.Nombre;
import com.medisync.medisync.domain.valueobjects.Telefono;

@ExtendWith(MockitoExtension.class)
class EliminarGestorUseCaseTest {

    @Mock
    private IGestorRepository gestorRepository;

    @InjectMocks
    private EliminarGestorUseCase eliminarGestorUseCase;

    @Test
    void deberiaEliminarGestorExitosamente() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        
        Nombre nombre = new Nombre("Juan Pérez");
        Nit nit = new Nit("123456789-0");
        Telefono telefono = new Telefono("3001234567");
        Email email = new Email("juan@example.com");
        Coordenadas coordenadas = new Coordenadas(
            new BigDecimal("4.60971"), 
            new BigDecimal("-74.08175")
        );
        
        Gestor gestor = Gestor.builder()
                .id(id)
                .nombre(nombre)
                .nit(nit)
                .direccion("Calle 123")
                .telefono(telefono)
                .email(email)
                .passwordHash("hash123")
                .coordenadas(coordenadas)
                .build();
        
        when(gestorRepository.findById(id)).thenReturn(Optional.of(gestor));
        
        // ACT
        eliminarGestorUseCase.ejecutar(id);
        
        // ASSERT
        verify(gestorRepository, times(1)).findById(id);
        verify(gestorRepository, times(1)).deleteById(id);
    }
    
    @Test
    void deberiaLanzarExcepcionCuandoGestorNoExisteAlEliminar() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        
        when(gestorRepository.findById(id)).thenReturn(Optional.empty());
        
        // ACT & ASSERT
        GestorNotFoundException ex = assertThrows(
                GestorNotFoundException.class,
                () -> eliminarGestorUseCase.ejecutar(id)
        );
        
        assertEquals("Gestor no encontrado con id: " + id, ex.getMessage());
        verify(gestorRepository, times(1)).findById(id);
        verify(gestorRepository, times(0)).deleteById(id);
    }
    
    @Test
    void deberiaVerificarQueElGestorExisteAntesDeEliminar() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        
        Nombre nombre = new Nombre("María García");
        Nit nit = new Nit("987654321-0");
        Telefono telefono = new Telefono("3009876543");
        Email email = new Email("maria@example.com");
        Coordenadas coordenadas = new Coordenadas(
            new BigDecimal("4.7110"), 
            new BigDecimal("-74.0721")
        );
        
        Gestor gestor = Gestor.builder()
                .id(id)
                .nombre(nombre)
                .nit(nit)
                .direccion("Carrera 45")
                .telefono(telefono)
                .email(email)
                .passwordHash("hash456")
                .coordenadas(coordenadas)
                .build();
        
        when(gestorRepository.findById(id)).thenReturn(Optional.of(gestor));
        
        // ACT
        eliminarGestorUseCase.ejecutar(id);
        
        // ASSERT
        verify(gestorRepository, times(1)).findById(id);
        verify(gestorRepository, times(1)).deleteById(id);
    }
}