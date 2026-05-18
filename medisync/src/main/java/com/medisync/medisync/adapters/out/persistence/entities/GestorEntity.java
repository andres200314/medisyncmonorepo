    package com.medisync.medisync.adapters.out.persistence.entities;

    import java.math.BigDecimal;
    import java.util.UUID;

    import com.medisync.medisync.domain.enums.EstadoGestor;

    import jakarta.persistence.Column;
    import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

    @Entity
    @Table(name = "gestores")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class GestorEntity {

        @Id
        @GeneratedValue
        private UUID id;

        @Column(nullable = false)
        private String nombre;

        @Column(nullable = false, unique = true)
        private String nit;

        private String direccion;

        private String telefono;

        @Column(nullable = false, unique = true)
        private String email;

        @Column(name = "password_hash", nullable = false)
        private String passwordHash;

        @Column(nullable = false)
        @Enumerated(EnumType.STRING)
        private EstadoGestor estado;

        private BigDecimal latitud;

        private BigDecimal longitud;
    }
