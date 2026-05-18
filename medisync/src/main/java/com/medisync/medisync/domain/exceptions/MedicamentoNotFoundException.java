package com.medisync.medisync.domain.exceptions;

public class MedicamentoNotFoundException extends RuntimeException {
    public MedicamentoNotFoundException(String id) {
        super("Medicamento no encontrado con id: " + id);
    }
}