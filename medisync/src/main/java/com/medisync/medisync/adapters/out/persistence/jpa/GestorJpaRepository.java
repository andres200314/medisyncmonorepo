package com.medisync.medisync.adapters.out.persistence.jpa;
 
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medisync.medisync.adapters.out.persistence.entities.GestorEntity;
 
public interface GestorJpaRepository extends JpaRepository<GestorEntity, UUID> {
    Optional<GestorEntity> findByEmail(String email);
}
