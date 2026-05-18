package com.medisync.medisync.application.usecases.gestor;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.medisync.medisync.domain.exceptions.GestorNotFoundException;
import com.medisync.medisync.domain.models.Gestor;
import com.medisync.medisync.domain.repositories.IGestorRepository;
import com.medisync.medisync.domain.valueobjects.Coordenadas;
import com.medisync.medisync.domain.valueobjects.Email;
import com.medisync.medisync.domain.valueobjects.Nit;
import com.medisync.medisync.domain.valueobjects.Nombre;
import com.medisync.medisync.domain.valueobjects.Telefono;

@ExtendWith(MockitoExtension.class)
class ActualizarGestorUseCaseTest {

    @Mock
    private IGestorRepository gestorRepository;

    @InjectMocks
    private ActualizarGestorUseCase actualizarGestorUseCase;

    @Test
    void deberiaActualizarGestorExitosamente() {
        // ARRANGE
        UUID id = UUID.randomUUID();

        Nombre nombreOriginal = new Nombre("Juan Pérez");
        Nit nitOriginal = new Nit("123456789-0");
        Telefono telefonoOriginal = new Telefono("3001234567");
        Email emailOriginal = new Email("juan@example.com");
        Coordenadas coordenadasOriginal = new Coordenadas(
                new BigDecimal("4.60971"),
                new BigDecimal("-74.08175")
        );

        Gestor existente = Gestor.builder()
                .id(id)
                .nombre(nombreOriginal)
                .nit(nitOriginal)
                .direccion("Calle 123")
                .telefono(telefonoOriginal)
                .email(emailOriginal)
                .passwordHash("hash_antiguo")
                .coordenadas(coordenadasOriginal)
                .build();

        String nombreNuevo = "Juan Carlos Pérez";
        String direccionNueva = "Carrera 45";
        String telefonoNuevo = "3007654321";
        String emailNuevo = "juancarlos@example.com";
        double latitudNueva = 4.60971;
        double longitudNueva = -74.08175;

        Gestor gestorGuardado = Gestor.builder()
                .id(id)
                .nombre(new Nombre(nombreNuevo))
                .nit(nitOriginal)  // Nit no cambia
                .direccion(direccionNueva)
                .telefono(new Telefono(telefonoNuevo))
                .email(new Email(emailNuevo))
                .passwordHash("hash_antiguo")  // La contraseña no cambia
                .coordenadas(new Coordenadas(BigDecimal.valueOf(latitudNueva), BigDecimal.valueOf(longitudNueva)))
                .build();

        when(gestorRepository.findById(id)).thenReturn(Optional.of(existente));
        when(gestorRepository.save(any(Gestor.class))).thenReturn(gestorGuardado);

        // ACT
        Gestor resultado = actualizarGestorUseCase.ejecutar(
                id, nombreNuevo, direccionNueva, telefonoNuevo, emailNuevo, latitudNueva, longitudNueva
        );

        // ASSERT
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals(nombreNuevo, resultado.getNombre().valor());
        assertEquals(nitOriginal.valor(), resultado.getNit().valor());
        assertEquals(direccionNueva, resultado.getDireccion());
        assertEquals(telefonoNuevo, resultado.getTelefono().valor());
        assertEquals(emailNuevo, resultado.getEmail().valor());
        assertEquals("hash_antiguo", resultado.getPasswordHash()); // Contraseña no cambió

        verify(gestorRepository, times(1)).findById(id);
        verify(gestorRepository, times(1)).save(any(Gestor.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoGestorNoExiste() {
        // ARRANGE
        UUID id = UUID.randomUUID();

        when(gestorRepository.findById(id)).thenReturn(Optional.empty());

        // ACT & ASSERT
        GestorNotFoundException ex = assertThrows(
                GestorNotFoundException.class,
                () -> actualizarGestorUseCase.ejecutar(id, "Juan Pérez", "Calle 123",
                        "3001234567", "juan@example.com", 4.60971, -74.08175)
        );

        assertEquals("Gestor no encontrado con id: " + id, ex.getMessage());
        verify(gestorRepository, times(1)).findById(id);
        verify(gestorRepository, never()).save(any(Gestor.class));
    }

    @Test
    void deberiaMantenerValoresOriginalesCuandoAlgunosCamposVienenNulos() {
        // ARRANGE
        UUID id = UUID.randomUUID();

        Nombre nombreOriginal = new Nombre("Juan Pérez");
        Nit nitOriginal = new Nit("123456789-0");
        Telefono telefonoOriginal = new Telefono("3001234567");
        Email emailOriginal = new Email("juan@example.com");
        String direccionOriginal = "Calle 123";
        Coordenadas coordenadasOriginal = new Coordenadas(
                new BigDecimal("4.60971"),
                new BigDecimal("-74.08175")
        );

        Gestor existente = Gestor.builder()
                .id(id)
                .nombre(nombreOriginal)
                .nit(nitOriginal)
                .direccion(direccionOriginal)
                .telefono(telefonoOriginal)
                .email(emailOriginal)
                .passwordHash("hash_antiguo")
                .coordenadas(coordenadasOriginal)
                .build();

        // Solo actualizar nombre y dirección, el resto debe mantenerse
        String nombreNuevo = "Juan Carlos Pérez";
        String direccionNueva = "Carrera 45";

        Gestor gestorGuardado = Gestor.builder()
                .id(id)
                .nombre(new Nombre(nombreNuevo))
                .nit(nitOriginal)
                .direccion(direccionNueva)
                .telefono(telefonoOriginal)  // Se mantiene
                .email(emailOriginal)        // Se mantiene
                .passwordHash("hash_antiguo")
                .coordenadas(coordenadasOriginal)  // Se mantiene
                .build();

        when(gestorRepository.findById(id)).thenReturn(Optional.of(existente));
        when(gestorRepository.save(any(Gestor.class))).thenReturn(gestorGuardado);

        // ACT - Pasar null en algunos campos
        Gestor resultado = actualizarGestorUseCase.ejecutar(
                id, nombreNuevo, direccionNueva, null, null, 4.60971, -74.08175
        );

        // ASSERT
        assertNotNull(resultado);
        assertEquals(nombreNuevo, resultado.getNombre().valor());
        assertEquals(direccionNueva, resultado.getDireccion());
        assertEquals(telefonoOriginal.valor(), resultado.getTelefono().valor());
        assertEquals(emailOriginal.valor(), resultado.getEmail().valor());
        assertEquals(nitOriginal.valor(), resultado.getNit().valor());

        verify(gestorRepository, times(1)).save(any(Gestor.class));
    }

    @Test
    void deberiaActualizarSoloCamposPermitidos() {
        // ARRANGE
        UUID id = UUID.randomUUID();

        Nombre nombreOriginal = new Nombre("Juan Pérez");
        Nit nitOriginal = new Nit("123456789-0");
        Telefono telefonoOriginal = new Telefono("3001234567");
        Email emailOriginal = new Email("juan@example.com");

        Coordenadas coordenadas = new Coordenadas(BigDecimal.valueOf(4.60971), BigDecimal.valueOf(-74.08175));

        Gestor existente = Gestor.builder()
                .id(id)
                .nombre(nombreOriginal)
                .nit(nitOriginal)
                .direccion("Calle 123")
                .telefono(telefonoOriginal)
                .email(emailOriginal)
                .passwordHash("hash_antiguo")
                .coordenadas(coordenadas)
                .build();

        String telefonoNuevo = "3112223344";
        String emailNuevo = "nuevoemail@example.com";

        Gestor gestorGuardado = Gestor.builder()
                .id(id)
                .nombre(nombreOriginal)
                .nit(nitOriginal)
                .direccion("Calle 123")
                .telefono(new Telefono(telefonoNuevo))
                .email(new Email(emailNuevo))
                .passwordHash("hash_antiguo")
                .coordenadas(coordenadas)
                .build();

        when(gestorRepository.findById(id)).thenReturn(Optional.of(existente));
        when(gestorRepository.save(any(Gestor.class))).thenReturn(gestorGuardado);

        // ACT - Solo actualizar teléfono y email
        Gestor resultado = actualizarGestorUseCase.ejecutar(
                id, null, null, telefonoNuevo, emailNuevo, 4.60971, -74.08175
        );

        // ASSERT
        assertEquals(nombreOriginal.valor(), resultado.getNombre().valor());
        assertEquals(nitOriginal.valor(), resultado.getNit().valor());
        assertEquals(telefonoNuevo, resultado.getTelefono().valor());
        assertEquals(emailNuevo, resultado.getEmail().valor());
        assertEquals("Calle 123", resultado.getDireccion());

        verify(gestorRepository, times(1)).save(any(Gestor.class));
    }
}