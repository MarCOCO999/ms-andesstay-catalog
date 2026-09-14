package com.andesstay.ms_andesstay_catalog.service;

import com.andesstay.ms_andesstay_catalog.config.ImagesProperties;
import com.andesstay.ms_andesstay_catalog.web.dto.PresignImageRequest;
import com.andesstay.ms_andesstay_catalog.web.dto.PresignImageResponse;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
public class ImageUploadService {

    private final S3Presigner s3Presigner;
    private final ImagesProperties properties;

    public ImageUploadService(S3Presigner s3Presigner, ImagesProperties properties) {
        this.s3Presigner = s3Presigner;
        this.properties = properties;
    }

    /**
     * Genera una URL prefirmada de subida (PUT) para que el frontend suba la foto directo a S3,
     * sin que el binario pase por este servicio. La clave incluye un UUID para evitar colisiones
     * entre unidades que suban un archivo con el mismo nombre.
     */
    public PresignImageResponse presignUpload(PresignImageRequest request) {
        String extension = extensionOf(request.fileName());
        String objectKey = "units/" + UUID.randomUUID() + extension;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(properties.bucket())
                .key(objectKey)
                .contentType(request.contentType())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(properties.presignTtlSeconds()))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);
        String publicUrl = "https://" + properties.bucket() + ".s3." + properties.region() + ".amazonaws.com/" + objectKey;

        return new PresignImageResponse(presigned.url().toString(), publicUrl);
    }

    private String extensionOf(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot >= 0 ? fileName.substring(dot) : "";
    }
}
