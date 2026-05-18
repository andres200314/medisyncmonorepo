package com.medisync.medisync.infrastructure.config;

import com.medisync.medisync.application.usecases.gestor.*;
import com.medisync.medisync.application.usecases.inventario.*;
import com.medisync.medisync.application.usecases.medicamento.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.medisync.medisync.domain.repositories.IGestorRepository;
import com.medisync.medisync.domain.repositories.IInventarioRepository;
import com.medisync.medisync.domain.repositories.IMedicamentoRepository;
import com.medisync.medisync.domain.services.IPasswordEncoder;
import com.medisync.medisync.infrastructure.security.JwtService;

@Configuration
public class BeanConfig {

    // ── Medicamento ───────────────────────────────────────────────────────────

    @Bean
    public CrearMedicamentoUseCase crearMedicamentoUseCase(IMedicamentoRepository medicamentoRepository) {
        return new CrearMedicamentoUseCase(medicamentoRepository);
    }

    @Bean
    public EliminarMedicamentoUseCase eliminarMedicamentoUseCase(IMedicamentoRepository medicamentoRepository) {
        return new EliminarMedicamentoUseCase(medicamentoRepository);
    }

    @Bean
    public ObtenerMedicamentosUseCase obtenerMedicamentosUseCase(IMedicamentoRepository medicamentoRepository) {
        return new ObtenerMedicamentosUseCase(medicamentoRepository);
    }

    @Bean
    public ObtenerMedicamentoPorIdUseCase obtenerMedicamentoPorIdUseCase(IMedicamentoRepository medicamentoRepository) {
        return new ObtenerMedicamentoPorIdUseCase(medicamentoRepository);
    }

    @Bean
    public ActualizarMedicamentoUseCase actualizarMedicamentoUseCase(IMedicamentoRepository medicamentoRepository) {
        return new ActualizarMedicamentoUseCase(medicamentoRepository);
    }

    @Bean
    public AgregarOActualizarMedicamentoInventarioUseCase agregarOActualizarMedicamentoInventarioUseCase(
            IMedicamentoRepository medicamentoRepository,
            CrearMedicamentoUseCase crearMedicamentoUseCase,
            AgregarMedicamentoInventarioUseCase agregarMedicamentoInventarioUseCase) {
        return new AgregarOActualizarMedicamentoInventarioUseCase(
                medicamentoRepository,
                crearMedicamentoUseCase,
                agregarMedicamentoInventarioUseCase);
    }

    // ── Inventario ────────────────────────────────────────────────────────────

    @Bean
    public CrearInventarioUseCase crearInventarioUseCase(IInventarioRepository inventarioRepository) {
        return new CrearInventarioUseCase(inventarioRepository);
    }

    @Bean
    public ObtenerInventariosUseCase obtenerInventariosUseCase(IInventarioRepository inventarioRepository) {
        return new ObtenerInventariosUseCase(inventarioRepository);
    }

    @Bean
    public ObtenerInventarioPorGestorUseCase obtenerInventarioPorGestorUseCase(IInventarioRepository inventarioRepository, IGestorRepository gestorRepository) {
        return new ObtenerInventarioPorGestorUseCase(inventarioRepository, gestorRepository);
    }

    @Bean
    public AgregarMedicamentoInventarioUseCase agregarMedicamentoInventarioUseCase(
            IInventarioRepository inventarioRepository,
            IMedicamentoRepository medicamentoRepository,
            IGestorRepository gestorRepository) {
        return new AgregarMedicamentoInventarioUseCase(inventarioRepository, medicamentoRepository, gestorRepository);
    }

    @Bean
    public QuitarMedicamentoInventarioUseCase quitarMedicamentoInventarioUseCase(
            IInventarioRepository inventarioRepository,
            IMedicamentoRepository medicamentoRepository,
            IGestorRepository gestorRepository) {
        return new QuitarMedicamentoInventarioUseCase(inventarioRepository, medicamentoRepository, gestorRepository);
    }

    @Bean
    public BuscarDisponibilidadMedicamentoUseCase buscarDisponibilidadMedicamentoUseCase(
            IInventarioRepository inventarioRepository,
            IMedicamentoRepository medicamentoRepository) {
        return new BuscarDisponibilidadMedicamentoUseCase(inventarioRepository, medicamentoRepository);
    }

    @Bean
    public AjustarStockUseCase ajustarStockUseCase(
            IInventarioRepository inventarioRepository,
            IMedicamentoRepository medicamentoRepository) {
        return new AjustarStockUseCase(inventarioRepository, medicamentoRepository);
    }

    @Bean
    public EstablecerStockUseCase establecerStockUseCase(
            IInventarioRepository inventarioRepository,
            IMedicamentoRepository medicamentoRepository) {
        return new EstablecerStockUseCase(inventarioRepository, medicamentoRepository);
    }

    @Bean
    public CambiarPrecioUseCase cambiarPrecioUseCase(
            IInventarioRepository inventarioRepository,
            IMedicamentoRepository medicamentoRepository) {
        return new CambiarPrecioUseCase(inventarioRepository, medicamentoRepository);
    }

    // ── Gestor ────────────────────────────────────────────────────────────────

    @Bean
    public RegistrarGestorUseCase registrarGestorUseCase(IGestorRepository gestorRepository,
                                                         IPasswordEncoder passwordEncoder,
                                                         CrearInventarioUseCase crearInventarioUseCase) {
        return new RegistrarGestorUseCase(gestorRepository, passwordEncoder, crearInventarioUseCase);
    }

    @Bean
    public LoginUseCase loginUseCase(IGestorRepository gestorRepository,
                                     IPasswordEncoder passwordEncoder,
                                     JwtService jwtService) {
        return new LoginUseCase(gestorRepository, passwordEncoder, jwtService);
    }

    @Bean
    public ObtenerGestoresUseCase obtenerGestoresUseCase(IGestorRepository gestorRepository) {
        return new ObtenerGestoresUseCase(gestorRepository);
    }

    @Bean
    public ObtenerGestorPorIdUseCase obtenerGestorPorIdUseCase(IGestorRepository gestorRepository) {
        return new ObtenerGestorPorIdUseCase(gestorRepository);
    }

    @Bean
    public ActualizarGestorUseCase actualizarGestorUseCase(IGestorRepository gestorRepository) {
        return new ActualizarGestorUseCase(gestorRepository);
    }

    @Bean
    public CambiarEstadoGestorUseCase cambiarEstadoGestorUseCase(IGestorRepository gestorRepository) {
        return new CambiarEstadoGestorUseCase(gestorRepository);
    }

    @Bean
    public CambiarPasswordGestorUseCase cambiarPasswordGestorUseCase(
            IGestorRepository gestorRepository,
            IPasswordEncoder passwordEncoder) {
        return new CambiarPasswordGestorUseCase(gestorRepository, passwordEncoder);
    }

    @Bean
    public EliminarGestorUseCase eliminarGestorUseCase(
            IGestorRepository gestorRepository,
            IInventarioRepository inventarioRepository) {
        return new EliminarGestorUseCase(gestorRepository, inventarioRepository);
    }
}