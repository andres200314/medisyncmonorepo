package com.medisync.medisync.adapters.out.persistence.mappers;

import com.medisync.medisync.domain.valueobjects.*;
import com.medisync.medisync.adapters.out.persistence.entities.GestorEntity;
import com.medisync.medisync.domain.models.Gestor;

public class GestorEntityMapper {

    public static Gestor toDomain(GestorEntity entity) {
        return Gestor.builder()
                .id(entity.getId())
                .nombre(new Nombre(entity.getNombre()))
                .nit(new Nit(entity.getNit()))
                .direccion(entity.getDireccion())
                .telefono(new Telefono(entity.getTelefono()))
                .email(new Email(entity.getEmail()))
                .passwordHash(entity.getPasswordHash())
                .coordenadas(new Coordenadas(entity.getLatitud(), entity.getLongitud()))
                .estado(entity.getEstado())
                .build();
    }

    public static GestorEntity toEntity(Gestor gestor) {
        return GestorEntity.builder()
                .id(gestor.getId())
                .nombre(gestor.getNombre().valor())
                .nit(gestor.getNit().valor())
                .direccion(gestor.getDireccion())
                .telefono(gestor.getTelefono().valor())
                .email(gestor.getEmail().valor())
                .passwordHash(gestor.getPasswordHash())
                .latitud(gestor.getCoordenadas().latitud())
                .longitud(gestor.getCoordenadas().longitud())
                .estado(gestor.getEstado())
                .build();
    }
}