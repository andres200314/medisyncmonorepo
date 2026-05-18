package com.medisync.medisync.application.usecases.gestor;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medisync.medisync.domain.enums.EstadoGestor;
import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.models.Gestor;
import com.medisync.medisync.domain.repositories.IGestorRepository;
import com.medisync.medisync.domain.services.IPasswordEncoder;
import com.medisync.medisync.domain.valueobjects.Coordenadas;
import com.medisync.medisync.domain.valueobjects.Email;
import com.medisync.medisync.domain.valueobjects.Nit;
import com.medisync.medisync.domain.valueobjects.Nombre;
import com.medisync.medisync.domain.valueobjects.Telefono;
import com.medisync.medisync.infrastructure.security.JwtService;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private IGestorRepository gestorRepository;

    @Mock
    private IPasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private LoginUseCase loginUseCase;

    private Gestor crearGestorActivo() {
        return Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(new Nombre("Farmacia Central"))
                .nit(new Nit("900123456-7"))
                .direccion("Calle 10 #20-30")
                .telefono(new Telefono("3001234567"))
                .email(new Email("farmacia@test.com"))
                .passwordHash("$2a$10$hasheado")
                .coordenadas(new Coordenadas(
                        new BigDecimal("6.2442"),
                        new BigDecimal("-75.5812")))
                .estado(EstadoGestor.ACTIVO)
                .build();
    }

    @Test
    void deberiaRetornarTokenCuandoCredencialesSonValidas() {
        // ARRANGE
        Gestor gestor = crearGestorActivo();
        String email = "farmacia@test.com";
        String password = "123456";
        String tokenEsperado = "eyJhbGciOiJIUzI1NiJ9.token";

        when(gestorRepository.findByEmail(email)).thenReturn(Optional.of(gestor));
        when(passwordEncoder.matches(password, gestor.getPasswordHash())).thenReturn(true);
        when(jwtService.generateToken(gestor.getId(), email, "GESTOR")).thenReturn(tokenEsperado);

        // ACT
        String tokenResultado = loginUseCase.ejecutar(email, password);

        // ASSERT
        assertNotNull(tokenResultado);
        assertEquals(tokenEsperado, tokenResultado);
    }

    @Test
    void deberiaLanzarExcepcionCuandoEmailNoExiste() {
        // ARRANGE
        String email = "noexiste@test.com";
        String password = "123456";

        when(gestorRepository.findByEmail(email)).thenReturn(Optional.empty());

        // ACT & ASSERT
        BusinessRuleViolationException ex = assertThrows(
                BusinessRuleViolationException.class,
                () -> loginUseCase.ejecutar(email, password)
        );

        assertEquals("Credenciales inválidas", ex.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionCuandoPasswordEsIncorrecto() {
        // ARRANGE
        Gestor gestor = crearGestorActivo();
        String email = "farmacia@test.com";
        String passwordIncorrecto = "wrongpassword";

        when(gestorRepository.findByEmail(email)).thenReturn(Optional.of(gestor));
        when(passwordEncoder.matches(passwordIncorrecto, gestor.getPasswordHash())).thenReturn(false);

        // ACT & ASSERT
        BusinessRuleViolationException ex = assertThrows(
                BusinessRuleViolationException.class,
                () -> loginUseCase.ejecutar(email, passwordIncorrecto)
        );

        assertEquals("Credenciales inválidas", ex.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionCuandoGestorNoEstaActivo() {
        // ARRANGE
        Gestor gestorInactivo = Gestor.builder()
                .id(UUID.randomUUID())
                .nombre(new Nombre("Farmacia Inactiva"))
                .nit(new Nit("900123456-7"))
                .direccion("Calle 1")
                .telefono(new Telefono("3001234567"))
                .email(new Email("inactiva@test.com"))
                .passwordHash("$2a$10$hasheado")
                .coordenadas(new Coordenadas(
                        new BigDecimal("6.2442"),
                        new BigDecimal("-75.5812")))
                .estado(EstadoGestor.INACTIVO)
                .build();

        String email = "inactiva@test.com";
        String password = "123456";

        when(gestorRepository.findByEmail(email)).thenReturn(Optional.of(gestorInactivo));

        // ACT & ASSERT
        BusinessRuleViolationException ex = assertThrows(
                BusinessRuleViolationException.class,
                () -> loginUseCase.ejecutar(email, password)
        );

        assertEquals("El gestor no está activo", ex.getMessage());
    }
}