package com.medisync.medisync.domain.models;

import java.util.UUID;

import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicamento {
    private UUID id;
    private String nombre;
    private Boolean requiereFormula;
    private String descripcion;


    public Medicamento(String nombre, Boolean requiereFormula, String descripcion) {
        this.nombre = nombre;
        this.requiereFormula = requiereFormula;
        this.descripcion = descripcion;
        validar();
        normalizarNombre();
    }

    public static Medicamento crear(String nombre, Boolean requiereFormula, String descripcion) {
        return new Medicamento(nombre, requiereFormula, descripcion);
    }

    public void validar() {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new BusinessRuleViolationException("El nombre no puede estar vacío");
        }
        if (nombre.trim().length() < 3) {
            throw new BusinessRuleViolationException("El nombre debe tener al menos 3 caracteres");
        }
        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+")) {
            throw new BusinessRuleViolationException("El nombre solo puede contener letras");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new BusinessRuleViolationException("La descripción no puede estar vacía");
        }
        if (requiereFormula && descripcion.trim().length() < 20) {
            throw new BusinessRuleViolationException("Un medicamento con fórmula debe tener una descripción detallada");
        }
    }

    public void normalizarNombre() {
        if (nombre != null && !nombre.trim().isEmpty()) {
            this.nombre = nombre.trim().substring(0, 1).toUpperCase()
                    + nombre.trim().substring(1).toLowerCase();
        }
    }

    public void actualizar(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        validar();
        normalizarNombre();
    }
}