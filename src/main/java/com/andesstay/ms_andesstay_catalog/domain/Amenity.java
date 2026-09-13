package com.andesstay.ms_andesstay_catalog.domain;

/**
 * Lista cerrada de valores agregados que puede tener una unidad. Se modela como enum
 * (en vez de texto libre) para que el frontend no pueda mandar valores con errores de
 * tipeo ni variantes que despues no calcen al filtrar/comparar.
 */
public enum Amenity {
    WIFI,
    DESAYUNO_INCLUIDO,
    ESTACIONAMIENTO,
    VISTA_MONTANA,
    VISTA_LAGO,
    JACUZZI,
    CALEFACCION,
    PET_FRIENDLY,
    COCINA_EQUIPADA,
    CHIMENEA,
    TERRAZA,
    ACCESO_SENDEROS
}
