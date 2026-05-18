package com.medisync.medisync.domain.valueobjects;

import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;

public record Cantidad(int valor) {

    public Cantidad {
        if (valor < 0) {
            throw new BusinessRuleViolationException("La cantidad no puede ser negativa");
        }
    }

    public static Cantidad of(int valor) {
        return new Cantidad(valor);
    }

    public boolean esCero() {
        return valor == 0;
    }

    public boolean tieneStock() {
        return valor > 0;
    }

    public boolean esPositiva() {
        return valor > 0;
    }

    public Cantidad sumar(int cantidad) {
        if (cantidad < 0) {
            throw new BusinessRuleViolationException("La cantidad a sumar debe ser positiva o cero");
        }
        return new Cantidad(this.valor + cantidad);
    }

    public Cantidad restar(int cantidad) {
        if (cantidad < 0) {
            throw new BusinessRuleViolationException("La cantidad a restar debe ser positiva o cero");
        }
        if (this.valor < cantidad) {
            throw new BusinessRuleViolationException("No hay suficiente stock. Stock actual: " + this.valor + ", solicitado: " + cantidad);
        }
        return new Cantidad(this.valor - cantidad);
    }

    @Override
    public String toString() {
        return String.valueOf(valor);
    }
}