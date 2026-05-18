package com.medisync.medisync.application.usecases.gestor;

import java.util.UUID;

import com.medisync.medisync.domain.exceptions.GestorNotFoundException;
import com.medisync.medisync.domain.exceptions.InvalidPasswordException;
import com.medisync.medisync.domain.models.Gestor;
import com.medisync.medisync.domain.repositories.IGestorRepository;
import com.medisync.medisync.domain.services.IPasswordEncoder;

public class CambiarPasswordGestorUseCase {

    private final IGestorRepository gestorRepository;
    private final IPasswordEncoder passwordEncoder;

    public CambiarPasswordGestorUseCase(IGestorRepository gestorRepository,
                                         IPasswordEncoder passwordEncoder) {
        this.gestorRepository = gestorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void ejecutar(UUID id, String passwordActual, String passwordNueva) {
        
        validarPassword(passwordActual, "La contraseña actual no puede estar vacía");
        validarPassword(passwordNueva, "La nueva contraseña no puede estar vacía");
        
        validarRequisitosPassword(passwordNueva);
        
        Gestor gestor = gestorRepository.findById(id)
                .orElseThrow(() -> new GestorNotFoundException(id.toString()));
        
        if (!gestor.puedeIniciarSesion()) {
            throw new InvalidPasswordException("El gestor no está activo. No puede cambiar la contraseña");
        }
        
        if (!passwordEncoder.matches(passwordActual, gestor.getPasswordHash())) {
            throw new InvalidPasswordException("La contraseña actual es incorrecta");
        }
        
        if (passwordEncoder.matches(passwordNueva, gestor.getPasswordHash())) {
            throw new InvalidPasswordException("La nueva contraseña debe ser diferente a la actual");
        }
        
        String nuevaPasswordHash = passwordEncoder.encode(passwordNueva);
        
        gestor.cambiarPassword(nuevaPasswordHash);
        
        gestorRepository.save(gestor);
    }
    
    private void validarPassword(String password, String mensajeError) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException(mensajeError);
        }
    }
    
    private void validarRequisitosPassword(String password) {
        if (password.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }
        
        boolean tieneMayuscula = password.chars().anyMatch(Character::isUpperCase);
        boolean tieneMinuscula = password.chars().anyMatch(Character::isLowerCase);
        boolean tieneDigito = password.chars().anyMatch(Character::isDigit);
        
        if (!tieneMayuscula || !tieneMinuscula || !tieneDigito) {
            throw new IllegalArgumentException(
                "La contraseña debe contener al menos una mayúscula, una minúscula y un número"
            );
        }
    }
}