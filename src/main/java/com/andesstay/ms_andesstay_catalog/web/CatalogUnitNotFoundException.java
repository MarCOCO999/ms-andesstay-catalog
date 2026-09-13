package com.andesstay.ms_andesstay_catalog.web;

public class CatalogUnitNotFoundException extends RuntimeException {

    public CatalogUnitNotFoundException(Long id) {
        super("No existe una unidad de catalogo con id " + id);
    }
}
