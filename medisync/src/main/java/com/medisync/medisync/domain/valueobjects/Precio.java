package com.medisync.medisync.domain.valueobjects;


import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Precio(BigDecimal valor) {

    private static final int SCALE = 2;

    public Precio {
        if (valor == null) {
            throw new BusinessRuleViolationException("El precio no puede ser nulo");
        }

        if (valor.signum() < 0) {
            throw new BusinessRuleViolationException("El precio no puede ser negativo");
        }

        // normalizar escala (ej: 12500 -> 12500.00)
        valor = valor.setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static Precio of(BigDecimal valor) {
        return new Precio(valor);
    }

    public Precio sumar(Precio otro) {
        return new Precio(this.valor.add(otro.valor));
    }

    public Precio multiplicar(int cantidad) {
        if (cantidad < 0) {
            throw new BusinessRuleViolationException("La cantidad no puede ser negativa");
        }
        return new Precio(this.valor.multiply(BigDecimal.valueOf(cantidad)));
    }

    public boolean esMayorQue(Precio otro) {
        return this.valor.compareTo(otro.valor) > 0;
    }

    public boolean esCero() {
        return this.valor.compareTo(BigDecimal.ZERO) == 0;
    }
}
