package com.medisync.medisync.domain.valueobjects;


import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;

public record Nit(String valor) {

    public Nit {
        if (valor == null || valor.isBlank()) {
            throw new BusinessRuleViolationException("El NIT no puede ser nulo o vacío");
        }
        if (!valor.matches("^[0-9]{9,10}-[0-9]$")) {
            throw new BusinessRuleViolationException("El NIT no tiene un formato válido: " + valor);
        }
    }

    public static Nit of(String valor) {
        return new Nit(valor);
    }
}
