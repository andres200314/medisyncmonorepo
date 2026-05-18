package com.medisync.medisync.adapters.out.persistence.impl;

import com.medisync.medisync.adapters.out.persistence.entities.MedicamentoEntity;
import com.medisync.medisync.adapters.out.persistence.mappers.MedicamentoEntityMapper;
import com.medisync.medisync.domain.models.Medicamento;
import com.medisync.medisync.domain.repositories.IMedicamentoRepository;
import com.medisync.medisync.adapters.out.persistence.jpa.MedicamentoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MedicamentoRepositoryImpl implements IMedicamentoRepository {

    private final MedicamentoJpaRepository jpaRepository;
    private final MedicamentoEntityMapper entityMapper;

    @Override
    public Medicamento save(Medicamento medicamento) {
        MedicamentoEntity entity = entityMapper.toEntity(medicamento);
        MedicamentoEntity saved = jpaRepository.save(entity);
        return entityMapper.toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<Medicamento> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(entityMapper::toDomain);
    }

    @Override
    public List<Medicamento> findAll() {
        return jpaRepository.findAll().stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Medicamento> findByNombreContainingIgnoreCase(String nombre) {
        return jpaRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(entityMapper::toDomain)
                .collect(Collectors.toList());
    }

}
