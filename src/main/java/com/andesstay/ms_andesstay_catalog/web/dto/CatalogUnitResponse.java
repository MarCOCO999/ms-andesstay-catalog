package com.andesstay.ms_andesstay_catalog.web.dto;

import com.andesstay.ms_andesstay_catalog.domain.Amenity;
import com.andesstay.ms_andesstay_catalog.domain.CatalogUnit;
import com.andesstay.ms_andesstay_catalog.domain.UnitType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record CatalogUnitResponse(
        Long id,
        String code,
        UnitType type,
        Integer capacity,
        BigDecimal nightlyRate,
        Integer totalUnits,
        Integer availableUnits,
        boolean active,
        Set<Amenity> amenities,
        String imageUrl,
        Instant createdAt,
        Instant updatedAt
) {
    public static CatalogUnitResponse from(CatalogUnit unit) {
        return new CatalogUnitResponse(
                unit.getId(), unit.getCode(), unit.getType(), unit.getCapacity(), unit.getNightlyRate(),
                unit.getTotalUnits(), unit.getAvailableUnits(), unit.isActive(), unit.getAmenities(),
                unit.getImageUrl(), unit.getCreatedAt(), unit.getUpdatedAt());
    }
}
