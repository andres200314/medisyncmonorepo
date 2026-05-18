package com.medisync.medisync.application.usecases.gestor;
 
import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.models.Gestor;
import com.medisync.medisync.domain.repositories.IGestorRepository;
import com.medisync.medisync.domain.services.IPasswordEncoder;
import com.medisync.medisync.infrastructure.security.JwtService;
 
public class LoginUseCase {
 
    private final IGestorRepository gestorRepository;
    private final IPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
 
    public LoginUseCase(IGestorRepository gestorRepository,
                        IPasswordEncoder passwordEncoder,
                        JwtService jwtService) {
        this.gestorRepository = gestorRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
 
    public String ejecutar(String email, String password) {
        Gestor gestor = gestorRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessRuleViolationException("Credenciales inválidas"));
 
        if (!gestor.puedeIniciarSesion()) {
            throw new BusinessRuleViolationException("El gestor no está activo");
        }
 
        if (!passwordEncoder.matches(password, gestor.getPasswordHash())) {
            throw new BusinessRuleViolationException("Credenciales inválidas");
        }
 
        return jwtService.generateToken(gestor.getId(), gestor.getEmail().valor(), "GESTOR");
    }
}
