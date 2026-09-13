package com.andesstay.ms_andesstay_catalog.web.dto;

import com.andesstay.ms_andesstay_catalog.domain.Amenity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.Set;

/** PUT /api/catalog/units/{id}: actualiza tarifa, disponibilidad y valores agregados de una unidad existente. */
public record UpdateCatalogUnitRequest(
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal nightlyRate,
        @NotNull @PositiveOrZero Integer availableUnits,
        @NotNull Boolean active,
        /** Opcional: si viene null se deja la lista de amenities tal como estaba. */
        Set<Amenity> amenities
) {
}
