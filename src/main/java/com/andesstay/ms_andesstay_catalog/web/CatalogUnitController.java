package com.andesstay.ms_andesstay_catalog.web;

import com.andesstay.ms_andesstay_catalog.domain.CatalogUnit;
import com.andesstay.ms_andesstay_catalog.domain.UnitType;
import com.andesstay.ms_andesstay_catalog.service.CatalogUnitService;
import com.andesstay.ms_andesstay_catalog.service.ImageUploadService;
import com.andesstay.ms_andesstay_catalog.web.dto.CatalogUnitResponse;
import com.andesstay.ms_andesstay_catalog.web.dto.CreateCatalogUnitRequest;
import com.andesstay.ms_andesstay_catalog.web.dto.PresignImageRequest;
import com.andesstay.ms_andesstay_catalog.web.dto.PresignImageResponse;
import com.andesstay.ms_andesstay_catalog.web.dto.UpdateCatalogUnitRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/units")
public class CatalogUnitController {

    private final CatalogUnitService service;
    private final ImageUploadService imageUploadService;

    public CatalogUnitController(CatalogUnitService service, ImageUploadService imageUploadService) {
        this.service = service;
        this.imageUploadService = imageUploadService;
    }

    /**
     * El Admin llama esto antes de crear/editar una unidad para obtener una URL de subida
     * directa a S3; el binario de la imagen nunca pasa por este servicio.
     */
    @PostMapping("/images/presign")
    @PreAuthorize("hasRole('Admin')")
    public PresignImageResponse presignImage(@Valid @RequestBody PresignImageRequest request) {
        return imageUploadService.presignUpload(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('Admin', 'Operador', 'Cliente')")
    public List<CatalogUnitResponse> search(@RequestParam(required = false) UnitType type,
                                             @RequestParam(required = false) Boolean active) {
        return service.search(type, active).stream()
                .map(CatalogUnitResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin', 'Operador', 'Cliente')")
    public CatalogUnitResponse getById(@PathVariable Long id) {
        return CatalogUnitResponse.from(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<CatalogUnitResponse> create(@Valid @RequestBody CreateCatalogUnitRequest request) {
        CatalogUnit unit = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CatalogUnitResponse.from(unit));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public CatalogUnitResponse update(@PathVariable Long id, @Valid @RequestBody UpdateCatalogUnitRequest request) {
        return CatalogUnitResponse.from(service.update(id, request));
    }

    /**
     * Llamado internamente por ms-andesstay-reservations al confirmar una reserva (nunca
     * por el BFF/un usuario final). Operador tambien puede confirmar reservas, por eso
     * este endpoint permite ese rol ademas de Admin, a diferencia del resto de escrituras
     * de catalogo que son exclusivas de Admin.
     */
    @PostMapping("/{id}/reserve")
    @PreAuthorize("hasAnyRole('Admin', 'Operador')")
    public CatalogUnitResponse reserve(@PathVariable Long id) {
        return CatalogUnitResponse.from(service.reserveUnit(id));
    }

    /** Contraparte de /reserve: libera la unidad al hacer checkout o cancelar una reserva ya confirmada. */
    @PostMapping("/{id}/release")
    @PreAuthorize("hasAnyRole('Admin', 'Operador')")
    public CatalogUnitResponse release(@PathVariable Long id) {
        return CatalogUnitResponse.from(service.releaseUnit(id));
    }
}
