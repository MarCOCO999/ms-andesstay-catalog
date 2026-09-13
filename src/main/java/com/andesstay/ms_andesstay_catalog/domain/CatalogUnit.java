package com.andesstay.ms_andesstay_catalog.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "CATALOG_UNITS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Codigo unico de la unidad, p. ej. "HAB-101" o "CAB-03". Referenciado por reservations como unitId. */
    @Column(name = "CODE", nullable = false, unique = true, length = 40)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false, length = 20)
    private UnitType type;

    @Column(name = "CAPACITY", nullable = false)
    private Integer capacity;

    @Column(name = "NIGHTLY_RATE", nullable = false, precision = 12, scale = 2)
    private BigDecimal nightlyRate;

    /** Inventario total de unidades identicas de este tipo (p. ej. 5 cabanas iguales). */
    @Column(name = "TOTAL_UNITS", nullable = false)
    private Integer totalUnits;

    /** Cupos actualmente disponibles; disminuye cuando una reserva de este tipo se confirma. */
    @Column(name = "AVAILABLE_UNITS", nullable = false)
    private Integer availableUnits;

    @Column(name = "ACTIVE", nullable = false)
    private boolean active;

    /**
     * Lista cerrada (enum Amenity) para el "valor agregado" de la unidad: wifi, desayuno
     * incluido, vista al lago, etc. Un Set en vez de List porque no importa el orden y
     * no tiene sentido repetir el mismo valor dos veces.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "CATALOG_UNIT_AMENITIES", joinColumns = @JoinColumn(name = "UNIT_ID"))
    @Enumerated(EnumType.STRING)
    @Column(name = "AMENITY", nullable = false, length = 30)
    @Builder.Default
    private Set<Amenity> amenities = new LinkedHashSet<>();

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.availableUnits == null) {
            this.availableUnits = this.totalUnits;
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
