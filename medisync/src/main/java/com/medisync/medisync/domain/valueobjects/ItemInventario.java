package com.medisync.medisync.domain.valueobjects;

import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.models.Medicamento;

public record ItemInventario(
        Medicamento medicamento,
        Cantidad cantidad,
        Precio precioUnitario
) {

    public ItemInventario {
        if (medicamento == null) {
            throw new BusinessRuleViolationException("El medicamento no puede ser nulo");
        }
        if (cantidad == null) {
            throw new BusinessRuleViolationException("La cantidad no puede ser nula");
        }
        if (precioUnitario == null) {
            throw new BusinessRuleViolationException("El precio no puede ser nulo");
        }
    }

    public ItemInventario aumentarStock(int cantidad) {
        return new ItemInventario(
                this.medicamento,
                this.cantidad.sumar(cantidad),
                this.precioUnitario
        );
    }

    public ItemInventario reducirStock(int cantidad) {
        return new ItemInventario(
                this.medicamento,
                this.cantidad.restar(cantidad),
                this.precioUnitario
        );
    }

    public ItemInventario cambiarPrecio(Precio nuevoPrecio) {
        return new ItemInventario(
                this.medicamento,
                this.cantidad,
                nuevoPrecio
        );
    }

    public boolean tieneStock() {
        return cantidad.tieneStock();
    }

    public boolean esDelMismoMedicamento(Medicamento otro) {
        return this.medicamento.getId().equals(otro.getId());
    }
}