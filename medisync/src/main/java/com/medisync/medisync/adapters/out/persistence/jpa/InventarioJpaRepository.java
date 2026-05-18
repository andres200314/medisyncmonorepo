package com.medisync.medisync.adapters.out.persistence.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medisync.medisync.adapters.out.persistence.entities.InventarioEntity;

@Repository
public interface InventarioJpaRepository extends JpaRepository<InventarioEntity, UUID> {

    Optional<InventarioEntity> findByGestorId(UUID gestorId);
}