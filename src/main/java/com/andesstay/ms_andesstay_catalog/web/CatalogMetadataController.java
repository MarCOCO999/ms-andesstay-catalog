package com.andesstay.ms_andesstay_catalog.web;

import com.andesstay.ms_andesstay_catalog.domain.Amenity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Expone los valores permitidos del enum Amenity para que el frontend arme el selector sin duplicarlos a mano. */
@RestController
public class CatalogMetadataController {

    @GetMapping("/api/catalog/amenities")
    @PreAuthorize("hasAnyRole('Admin', 'Operador', 'Cliente')")
    public List<Amenity> amenities() {
        return List.of(Amenity.values());
    }
}
