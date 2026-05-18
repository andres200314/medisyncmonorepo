package com.medisync.medisync.domain.valueobjects;

import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;

public record Email(String valor) {

    public Email(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new BusinessRuleViolationException("El email no puede ser nulo o vacío");
        }
        if (!valor.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new BusinessRuleViolationException("El email no tiene un formato válido: " + valor);
        }
        this.valor = valor.toLowerCase();
    }

    public static Email of(String valor) {
        return new Email(valor);
    }
}
