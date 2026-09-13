package com.andesstay.ms_andesstay_catalog.repository;

import com.andesstay.ms_andesstay_catalog.domain.CatalogUnit;
import com.andesstay.ms_andesstay_catalog.domain.UnitType;
import org.springframework.data.jpa.domain.Specification;

public final class CatalogUnitSpecifications {

    private CatalogUnitSpecifications() {
    }

    public static Specification<CatalogUnit> hasType(UnitType type) {
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    public static Specification<CatalogUnit> isActive(boolean active) {
        return (root, query, cb) -> cb.equal(root.get("active"), active);
    }
}
