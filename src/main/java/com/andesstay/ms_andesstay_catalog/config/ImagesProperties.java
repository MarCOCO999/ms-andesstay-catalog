package com.andesstay.ms_andesstay_catalog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "andesstay.images")
public record ImagesProperties(
        String bucket,
        String region,
        /** Segundos que dura vigente la URL prefirmada de subida. */
        long presignTtlSeconds
) {
}
