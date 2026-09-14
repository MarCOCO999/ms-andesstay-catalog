package com.andesstay.ms_andesstay_catalog.web.dto;

public record PresignImageResponse(
        /** URL firmada: el frontend hace un PUT del archivo directo a esta URL, sin pasar por el backend. */
        String uploadUrl,
        /** URL publica final, para guardar como imageUrl de la unidad una vez subida la foto. */
        String publicUrl
) {
}
