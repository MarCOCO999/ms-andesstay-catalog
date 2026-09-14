package com.andesstay.ms_andesstay_catalog.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PresignImageRequest(
        @NotBlank String fileName,
        /** Solo se aceptan imagenes; evita que se use el bucket para subir cualquier tipo de archivo. */
        @NotBlank @Pattern(regexp = "^image/(png|jpeg|jpg|webp)$", message = "Solo se aceptan imagenes PNG, JPEG o WEBP")
        String contentType
) {
}
