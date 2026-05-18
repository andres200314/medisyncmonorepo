package com.medisync.medisync.adapters.out.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "inventario_medicamento",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"inventario_id", "medicamento_id"})
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioMedicamentoEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "inventario_id", nullable = false)
    private InventarioEntity inventario;

    @ManyToOne
    @JoinColumn(name = "medicamento_id", nullable = false)
    private MedicamentoEntity medicamento;

    private Integer cantidad;

    private BigDecimal precioUnitario;
}
