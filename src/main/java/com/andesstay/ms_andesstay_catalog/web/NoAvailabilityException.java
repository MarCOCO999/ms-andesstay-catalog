package com.andesstay.ms_andesstay_catalog.web;

public class NoAvailabilityException extends RuntimeException {

    public NoAvailabilityException(Long unitId) {
        super("No hay disponibilidad para la unidad " + unitId);
    }
}
