package com.medisync.medisync.domain.models;

import com.medisync.medisync.domain.exceptions.BusinessRuleViolationException;
import com.medisync.medisync.domain.valueobjects.Cantidad;
import com.medisync.medisync.domain.valueobjects.ItemInventario;
import com.medisync.medisync.domain.valueobjects.Precio;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventario {

    private UUID id;
    private Gestor gestor;

    @Builder.Default
    private List<ItemInventario> items = new ArrayList<>();


    public void agregarMedicamento(Medicamento medicamento, Cantidad cantidad, Precio precio) {

        validarGestorActivo();
        validarMedicamento(medicamento);

        ItemInventario existente = buscarItem(medicamento);

        if (existente != null) {

            if (!existente.precioUnitario().equals(precio)) {
                throw new BusinessRuleViolationException(
                        "No se puede agregar el mismo medicamento con diferente precio."
                );
            }

            ItemInventario actualizado = existente.aumentarStock(cantidad.valor());
            actualizarItem(existente, actualizado);

        } else {
            items.add(new ItemInventario(medicamento, cantidad, precio));
        }
    }

    public void ajustarStock(Medicamento medicamento, int delta) {
        validarGestorActivo();
        validarMedicamento(medicamento);

        ItemInventario existente = buscarItem(medicamento);
        if (existente == null) {
            throw new BusinessRuleViolationException("El medicamento no existe en el inventario");
        }

        ItemInventario actualizado = delta >= 0
                ? existente.aumentarStock(delta)
                : existente.reducirStock(-delta);
        actualizarItem(existente, actualizado);
    }

    public void establecerStock(Medicamento medicamento, int cantidad) {
        validarGestorActivo();
        validarMedicamento(medicamento);

        ItemInventario existente = buscarItem(medicamento);
        if (existente == null) {
            throw new BusinessRuleViolationException("El medicamento no existe en el inventario");
        }

        ItemInventario actualizado = new ItemInventario(
                existente.medicamento(),
                Cantidad.of(cantidad),
                existente.precioUnitario()
        );
        actualizarItem(existente, actualizado);
    }

    public void reducirStock(Medicamento medicamento, int cantidad) {

        validarGestorActivo();
        validarMedicamento(medicamento);

        ItemInventario existente = buscarItem(medicamento);

        if (existente == null) {
            throw new BusinessRuleViolationException("El medicamento no existe en el inventario");
        }

        if (existente.cantidad().valor() < cantidad) {
            throw new BusinessRuleViolationException("No hay suficiente stock para reducir");
        }

        ItemInventario actualizado = existente.reducirStock(cantidad);
        actualizarItem(existente, actualizado);
    }

    public void cambiarPrecio(Medicamento medicamento, Precio nuevoPrecio) {

        validarGestorActivo();
        validarMedicamento(medicamento);

        ItemInventario existente = buscarItem(medicamento);

        if (existente == null) {
            throw new BusinessRuleViolationException("El medicamento no existe en el inventario");
        }

        ItemInventario actualizado = existente.cambiarPrecio(nuevoPrecio);
        actualizarItem(existente, actualizado);
    }

    /**
     * Quita el ítem del inventario de esta farmacia. No elimina el medicamento del catálogo global.
     */
    public void quitarMedicamento(Medicamento medicamento) {
        validarGestorActivo();
        validarMedicamento(medicamento);

        boolean removido = items.removeIf(item -> item.esDelMismoMedicamento(medicamento));
        if (!removido) {
            throw new BusinessRuleViolationException("El medicamento no existe en el inventario");
        }
    }

    public boolean tieneStock(Medicamento medicamento) {
        validarMedicamento(medicamento);
        ItemInventario existente = buscarItem(medicamento);
        return existente != null && existente.tieneStock();
    }

    public boolean estaDisponibleParaUsuarios() {
        return gestor.esVisibleParaUsuarios() && tieneAlMenosUnItemConStock();
    }

    private void validarGestorActivo() {
        if (gestor == null) {
            throw new BusinessRuleViolationException("El inventario debe tener un gestor");
        }

        if (!gestor.estaActivo()) {
            throw new BusinessRuleViolationException("El gestor debe estar activo para modificar el inventario");
        }
    }

    private void validarMedicamento(Medicamento medicamento) {
        if (medicamento == null) {
            throw new BusinessRuleViolationException("El medicamento no puede ser nulo");
        }
    }

    public ItemInventario buscarItem(Medicamento medicamento) {
        for (ItemInventario item : items) {
            if (item.esDelMismoMedicamento(medicamento)) {
                return item;
            }
        }
        return null;
    }

    private void actualizarItem(ItemInventario original, ItemInventario actualizado) {
        int index = items.indexOf(original);

        if (index == -1) {
            throw new IllegalStateException("Error interno: item no encontrado");
        }

        items.set(index, actualizado);
    }

    private boolean tieneAlMenosUnItemConStock() {
        for (ItemInventario item : items) {
            if (item.tieneStock()) {
                return true;
            }
        }
        return false;
    }

    public List<ItemInventario> obtenerItemsConStock() {
        return items.stream()
                .filter(ItemInventario::tieneStock)
                .collect(Collectors.toList());
    }
}