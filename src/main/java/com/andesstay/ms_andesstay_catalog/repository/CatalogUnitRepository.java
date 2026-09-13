package com.andesstay.ms_andesstay_catalog.repository;

import com.andesstay.ms_andesstay_catalog.domain.CatalogUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface CatalogUnitRepository extends JpaRepository<CatalogUnit, Long>, JpaSpecificationExecutor<CatalogUnit> {

    boolean existsByCode(String code);

    Optional<CatalogUnit> findByCode(String code);

    /**
     * Decremento atomico a nivel de fila: la condicion availableUnits > 0 en el propio UPDATE
     * evita el problema clasico de "leer, restar, escribir" (dos confirmaciones concurrentes
     * podrian leer el mismo valor y pisarse una a la otra). Devuelve cuantas filas se
     * modificaron: 0 significa "no hay disponibilidad" o "la unidad no existe".
     */
    @Modifying
    @Query("UPDATE CatalogUnit c SET c.availableUnits = c.availableUnits - 1, c.updatedAt = :now " +
            "WHERE c.id = :id AND c.availableUnits > 0")
    int decrementAvailability(@Param("id") Long id, @Param("now") Instant now);

    /** Mismo principio que decrementAvailability, pero acotado por arriba al inventario total. */
    @Modifying
    @Query("UPDATE CatalogUnit c SET c.availableUnits = c.availableUnits + 1, c.updatedAt = :now " +
            "WHERE c.id = :id AND c.availableUnits < c.totalUnits")
    int incrementAvailability(@Param("id") Long id, @Param("now") Instant now);
}
