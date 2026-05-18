package com.medisync.medisync.domain.exceptions;

public class GestorNotFoundException extends RuntimeException {
    public GestorNotFoundException(String id) {
        super("Gestor no encontrado con id: " + id);
    }
}
