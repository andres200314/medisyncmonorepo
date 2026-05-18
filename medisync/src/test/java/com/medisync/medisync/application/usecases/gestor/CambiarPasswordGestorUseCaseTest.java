package com.medisync.medisync.application.usecases.gestor;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medisync.medisync.domain.exceptions.GestorNotFoundException;
import com.medisync.medisync.domain.exceptions.InvalidPasswordException;
import com.medisync.medisync.domain.enums.EstadoGestor;
import com.medisync.medisync.domain.models.Gestor;
import com.medisync.medisync.domain.repositories.IGestorRepository;
import com.medisync.medisync.domain.services.IPasswordEncoder;
import com.medisync.medisync.domain.valueobjects.Coordenadas;
import com.medisync.medisync.domain.valueobjects.Email;
import com.medisync.medisync.domain.valueobjects.Nit;
import com.medisync.medisync.domain.valueobjects.Nombre;
import com.medisync.medisync.domain.valueobjects.Telefono;

@ExtendWith(MockitoExtension.class)
class CambiarPasswordGestorUseCaseTest {

    @Mock
    private IGestorRepository gestorRepository;

    @Mock
    private IPasswordEncoder passwordEncoder;

    @InjectMocks
    private CambiarPasswordGestorUseCase cambiarPasswordGestorUseCase;

    private UUID gestorId;
    private Gestor gestorActivo;
    private String passwordActual;
    private String passwordNueva;
    private String hashActual;
    private String hashNuevo;

    @BeforeEach
    void setUp() {
        gestorId = UUID.randomUUID();
        passwordActual = "Password123";
        passwordNueva = "NuevaPassword456";
        hashActual = "hash_actual_encriptado";
        hashNuevo = "hash_nuevo_encriptado";

        Nombre nombre = new Nombre("Juan Pérez");
        Nit nit = new Nit("123456789-0");
        Telefono telefono = new Telefono("3001234567");
        Email email = new Email("juan@example.com");
        Coordenadas coordenadas = new Coordenadas(
            new BigDecimal("4.60971"), 
            new BigDecimal("-74.08175")
        );

        gestorActivo = Gestor.builder()
                .id(gestorId)
                .nombre(nombre)
                .nit(nit)
                .direccion("Calle 123")
                .telefono(telefono)
                .email(email)
                .passwordHash(hashActual)
                .coordenadas(coordenadas)
                .estado(EstadoGestor.ACTIVO)
                .build();
    }

    @Test
    void deberiaCambiarPasswordExitosamente() {
        // ARRANGE
        when(gestorRepository.findById(gestorId)).thenReturn(Optional.of(gestorActivo));
        when(passwordEncoder.matches(passwordActual, hashActual)).thenReturn(true);
        when(passwordEncoder.matches(passwordNueva, hashActual)).thenReturn(false);
        when(passwordEncoder.encode(passwordNueva)).thenReturn(hashNuevo);
        when(gestorRepository.save(gestorActivo)).thenReturn(gestorActivo);

        // ACT
        cambiarPasswordGestorUseCase.ejecutar(gestorId, passwordActual, passwordNueva);

        // ASSERT
        verify(gestorRepository, times(1)).findById(gestorId);
        verify(passwordEncoder, times(1)).matches(passwordActual, hashActual);
        verify(passwordEncoder, times(1)).matches(passwordNueva, hashActual);
        verify(passwordEncoder, times(1)).encode(passwordNueva);
        verify(gestorRepository, times(1)).save(gestorActivo);
        
        assertEquals(hashNuevo, gestorActivo.getPasswordHash());
    }

    @Test
    void deberiaLanzarExcepcionCuandoGestorNoExiste() {
        // ARRANGE
        when(gestorRepository.findById(gestorId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        GestorNotFoundException ex = assertThrows(
                GestorNotFoundException.class,
                () -> cambiarPasswordGestorUseCase.ejecutar(gestorId, passwordActual, passwordNueva)
        );

        assertEquals("Gestor no encontrado con id: " + gestorId, ex.getMessage());
        verify(gestorRepository, times(1)).findById(gestorId);
        verify(passwordEncoder, never()).matches(org.mockito.ArgumentMatchers.anyString(), 
                                                  org.mockito.ArgumentMatchers.anyString());
        verify(passwordEncoder, never()).encode(org.mockito.ArgumentMatchers.anyString());
        verify(gestorRepository, never()).save(org.mockito.ArgumentMatchers.any(Gestor.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoGestorEstaSuspendido() {
        // ARRANGE
        Gestor gestorSuspendido = Gestor.builder()
                .id(gestorId)
                .nombre(gestorActivo.getNombre())
                .nit(gestorActivo.getNit())
                .direccion(gestorActivo.getDireccion())
                .telefono(gestorActivo.getTelefono())
                .email(gestorActivo.getEmail())
                .passwordHash(hashActual)
                .coordenadas(gestorActivo.getCoordenadas())
                .estado(EstadoGestor.SUSPENDIDO)
                .build();
        
        when(gestorRepository.findById(gestorId)).thenReturn(Optional.of(gestorSuspendido));

        // ACT & ASSERT
        InvalidPasswordException ex = assertThrows(
                InvalidPasswordException.class,
                () -> cambiarPasswordGestorUseCase.ejecutar(gestorId, passwordActual, passwordNueva)
        );

        assertEquals("El gestor no está activo. No puede cambiar la contraseña", ex.getMessage());
        verify(gestorRepository, times(1)).findById(gestorId);
        verify(passwordEncoder, never()).matches(org.mockito.ArgumentMatchers.anyString(), 
                                                  org.mockito.ArgumentMatchers.anyString());
        verify(gestorRepository, never()).save(org.mockito.ArgumentMatchers.any(Gestor.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoPasswordActualEsIncorrecta() {
        // ARRANGE
        when(gestorRepository.findById(gestorId)).thenReturn(Optional.of(gestorActivo));
        when(passwordEncoder.matches(passwordActual, hashActual)).thenReturn(false);

        // ACT & ASSERT
        InvalidPasswordException ex = assertThrows(
                InvalidPasswordException.class,
                () -> cambiarPasswordGestorUseCase.ejecutar(gestorId, passwordActual, passwordNueva)
        );

        assertEquals("La contraseña actual es incorrecta", ex.getMessage());
        verify(gestorRepository, times(1)).findById(gestorId);
        verify(passwordEncoder, times(1)).matches(passwordActual, hashActual);
        verify(passwordEncoder, never()).matches(passwordNueva, hashActual);
        verify(passwordEncoder, never()).encode(org.mockito.ArgumentMatchers.anyString());
        verify(gestorRepository, never()).save(org.mockito.ArgumentMatchers.any(Gestor.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoPasswordNuevaEsIgualALaActual() {
        // ARRANGE
        when(gestorRepository.findById(gestorId)).thenReturn(Optional.of(gestorActivo));
        when(passwordEncoder.matches(passwordActual, hashActual)).thenReturn(true);
        when(passwordEncoder.matches(passwordNueva, hashActual)).thenReturn(true);

        // ACT & ASSERT
        InvalidPasswordException ex = assertThrows(
                InvalidPasswordException.class,
                () -> cambiarPasswordGestorUseCase.ejecutar(gestorId, passwordActual, passwordNueva)
        );

        assertEquals("La nueva contraseña debe ser diferente a la actual", ex.getMessage());
        verify(gestorRepository, times(1)).findById(gestorId);
        verify(passwordEncoder, times(1)).matches(passwordActual, hashActual);
        verify(passwordEncoder, times(1)).matches(passwordNueva, hashActual);
        verify(passwordEncoder, never()).encode(org.mockito.ArgumentMatchers.anyString());
        verify(gestorRepository, never()).save(org.mockito.ArgumentMatchers.any(Gestor.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoPasswordActualEsNull() {
        // ACT & ASSERT
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> cambiarPasswordGestorUseCase.ejecutar(gestorId, null, passwordNueva)
        );

        assertEquals("La contraseña actual no puede estar vacía", ex.getMessage());
        verify(gestorRepository, never()).findById(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoPasswordNuevaEsMuyCorta() {
        // ARRANGE
        String passwordCorta = "Corta12";

        // ACT & ASSERT
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> cambiarPasswordGestorUseCase.ejecutar(gestorId, passwordActual, passwordCorta)
        );

        assertEquals("La contraseña debe tener al menos 8 caracteres", ex.getMessage());
        verify(gestorRepository, never()).findById(org.mockito.ArgumentMatchers.any());
    }
}