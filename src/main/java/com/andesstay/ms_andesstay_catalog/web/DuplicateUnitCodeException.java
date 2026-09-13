package com.andesstay.ms_andesstay_catalog.web;

public class DuplicateUnitCodeException extends RuntimeException {

    public DuplicateUnitCodeException(String code) {
        super("Ya existe una unidad de catalogo con codigo " + code);
    }
}
