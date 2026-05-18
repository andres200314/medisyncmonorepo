package com.medisync.medisync.adapters.out.persistence.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.medisync.medisync.adapters.out.persistence.entities.GestorEntity;
import com.medisync.medisync.adapters.out.persistence.jpa.GestorJpaRepository;
import com.medisync.medisync.adapters.out.persistence.mappers.GestorEntityMapper;
import com.medisync.medisync.domain.models.Gestor;
import com.medisync.medisync.domain.repositories.IGestorRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class GestorRepositoryImpl implements IGestorRepository {

    private final GestorJpaRepository jpaRepository;

    @Override
    public Gestor save(Gestor gestor) {
        GestorEntity entity = GestorEntityMapper.toEntity(gestor);
        GestorEntity saved = jpaRepository.save(entity);
        return GestorEntityMapper.toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<Gestor> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(GestorEntityMapper::toDomain);
    }

    @Override
    public List<Gestor> findAll() {
        return jpaRepository.findAll().stream()
                .map(GestorEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Gestor> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(GestorEntityMapper::toDomain);
    }
}