package com.medisync.medisync.domain.valueobjects;

import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;

public record Nombre(String valor) {

    public Nombre {
        if (valor == null || valor.isBlank()) {
            throw new BusinessRuleViolationException("El nombre no puede ser nulo o vacío");
        }
        if (valor.trim().length() < 4) {
            throw new BusinessRuleViolationException("El nombre debe tener al menos 4 caracteres");
        }
        valor = valor.trim();
    }

    public static Nombre of(String valor) {
        return new Nombre(valor);
    }
}