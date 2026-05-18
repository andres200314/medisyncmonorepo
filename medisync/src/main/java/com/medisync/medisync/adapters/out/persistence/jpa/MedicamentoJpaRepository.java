package com.medisync.medisync.adapters.out.persistence.jpa;

import com.medisync.medisync.adapters.out.persistence.entities.MedicamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MedicamentoJpaRepository extends JpaRepository<MedicamentoEntity, UUID> {
    List<MedicamentoEntity> findByNombreContainingIgnoreCase(String nombre);
}
