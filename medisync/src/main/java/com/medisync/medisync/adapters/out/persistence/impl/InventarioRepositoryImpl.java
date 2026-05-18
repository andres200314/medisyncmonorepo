package com.medisync.medisync.adapters.out.persistence.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Set;

import com.medisync.medisync.adapters.out.persistence.entities.InventarioMedicamentoEntity;
import com.medisync.medisync.adapters.out.persistence.entities.MedicamentoEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.medisync.medisync.adapters.out.persistence.entities.InventarioEntity;
import com.medisync.medisync.adapters.out.persistence.jpa.InventarioJpaRepository;
import com.medisync.medisync.adapters.out.persistence.mappers.InventarioEntityMapper;
import com.medisync.medisync.domain.models.Inventario;
import com.medisync.medisync.domain.repositories.IInventarioRepository;

@Repository
public class InventarioRepositoryImpl implements IInventarioRepository {

    private final InventarioJpaRepository jpaRepository;

    // Constructor sin mapper
    public InventarioRepositoryImpl(InventarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public Inventario save(Inventario inventario) {
        InventarioEntity entity = inventario.getId() != null
                ? jpaRepository.findById(inventario.getId())
                  .orElse(InventarioEntityMapper.toEntity(inventario))
                : InventarioEntityMapper.toEntity(inventario);

        Set<UUID> idsDominio = inventario.getItems().stream()
                .map(item -> item.medicamento().getId())
                .collect(Collectors.toSet());

        List<InventarioMedicamentoEntity> aEliminar = entity.getItems().stream()
                .filter(invMed -> !idsDominio.contains(invMed.getMedicamento().getId()))
                .collect(Collectors.toList());
        entity.getItems().removeAll(aEliminar);

        inventario.getItems().forEach(item -> {
            boolean yaExiste = entity.getItems().stream()
                    .anyMatch(e -> e.getMedicamento().getId().equals(item.medicamento().getId()));

            if (yaExiste) {
                entity.getItems().stream()
                        .filter(e -> e.getMedicamento().getId().equals(item.medicamento().getId()))
                        .findFirst()
                        .ifPresent(e -> {
                            e.setCantidad(item.cantidad().valor());
                            e.setPrecioUnitario(item.precioUnitario().valor());
                        });
            } else {
                entity.getItems().add(InventarioMedicamentoEntity.builder()
                        .id(null)
                        .inventario(entity)
                        .medicamento(MedicamentoEntity.builder().id(item.medicamento().getId()).build())
                        .cantidad(item.cantidad().valor())
                        .precioUnitario(item.precioUnitario().valor())
                        .build());
            }
        });

        return InventarioEntityMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        if (!jpaRepository.existsById(id)) {
            throw new RuntimeException("Inventario no encontrado con ID: " + id);
        }
        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<Inventario> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(InventarioEntityMapper::toDomain);
    }

    @Override
    public List<Inventario> findAll() {
        return jpaRepository.findAll().stream()
                .map(InventarioEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Inventario> findByGestorId(UUID gestorId) {
        return jpaRepository.findByGestorId(gestorId)
                .map(InventarioEntityMapper::toDomain);
    }
}