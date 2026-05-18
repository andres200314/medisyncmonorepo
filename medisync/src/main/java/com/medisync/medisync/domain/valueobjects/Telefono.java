package com.medisync.medisync.domain.valueobjects;


import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;

public record Telefono(String valor) {

    public Telefono {
        if (valor == null || valor.isBlank()) {
            throw new BusinessRuleViolationException("El teléfono no puede ser nulo o vacío");
        }
        if (!valor.matches("^[1-9][0-9]{9}$")) {
            throw new BusinessRuleViolationException("El teléfono debe tener 10 dígitos y no empezar con 0: " + valor);
        }
    }

    public static Telefono of(String valor) {
        return new Telefono(valor);
    }

}
