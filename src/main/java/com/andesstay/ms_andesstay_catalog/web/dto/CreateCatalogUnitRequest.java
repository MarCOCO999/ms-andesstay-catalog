package com.andesstay.ms_andesstay_catalog.web.dto;

import com.andesstay.ms_andesstay_catalog.domain.Amenity;
import com.andesstay.ms_andesstay_catalog.domain.UnitType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Set;

public record CreateCatalogUnitRequest(
        @NotBlank String code,
        @NotNull UnitType type,
        @NotNull @Positive Integer capacity,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal nightlyRate,
        @NotNull @Positive Integer totalUnits,
        /** Opcional: si viene null se guarda sin valores agregados. */
        Set<Amenity> amenities
) {
}
