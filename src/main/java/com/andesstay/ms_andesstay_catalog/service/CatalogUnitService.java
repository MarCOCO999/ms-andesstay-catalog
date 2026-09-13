package com.andesstay.ms_andesstay_catalog.service;

import com.andesstay.ms_andesstay_catalog.domain.CatalogUnit;
import com.andesstay.ms_andesstay_catalog.domain.UnitType;
import com.andesstay.ms_andesstay_catalog.repository.CatalogUnitRepository;
import com.andesstay.ms_andesstay_catalog.repository.CatalogUnitSpecifications;
import com.andesstay.ms_andesstay_catalog.web.CatalogUnitNotFoundException;
import com.andesstay.ms_andesstay_catalog.web.DuplicateUnitCodeException;
import com.andesstay.ms_andesstay_catalog.web.InvalidCatalogUnitRequestException;
import com.andesstay.ms_andesstay_catalog.web.NoAvailabilityException;
import com.andesstay.ms_andesstay_catalog.web.dto.CreateCatalogUnitRequest;
import com.andesstay.ms_andesstay_catalog.web.dto.UpdateCatalogUnitRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class CatalogUnitService {

    private final CatalogUnitRepository repository;

    public CatalogUnitService(CatalogUnitRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CatalogUnit create(CreateCatalogUnitRequest request) {
        if (repository.existsByCode(request.code())) {
            throw new DuplicateUnitCodeException(request.code());
        }
        CatalogUnit unit = CatalogUnit.builder()
                .code(request.code())
                .type(request.type())
                .capacity(request.capacity())
                .nightlyRate(request.nightlyRate())
                .totalUnits(request.totalUnits())
                .availableUnits(request.totalUnits())
                .active(true)
                .amenities(request.amenities() != null ? new LinkedHashSet<>(request.amenities()) : new LinkedHashSet<>())
                .build();
        return repository.save(unit);
    }

    @Transactional(readOnly = true)
    public CatalogUnit findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new CatalogUnitNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<CatalogUnit> search(UnitType type, Boolean active) {
        Specification<CatalogUnit> spec = (root, query, cb) -> cb.conjunction();
        if (type != null) {
            spec = spec.and(CatalogUnitSpecifications.hasType(type));
        }
        if (active != null) {
            spec = spec.and(CatalogUnitSpecifications.isActive(active));
        }
        return repository.findAll(spec);
    }

    @Transactional
    public CatalogUnit update(Long id, UpdateCatalogUnitRequest request) {
        CatalogUnit unit = findById(id);
        if (request.availableUnits() > unit.getTotalUnits()) {
            throw new InvalidCatalogUnitRequestException(
                    "La disponibilidad (" + request.availableUnits() + ") no puede superar el inventario total (" + unit.getTotalUnits() + ")");
        }
        unit.setNightlyRate(request.nightlyRate());
        unit.setAvailableUnits(request.availableUnits());
        unit.setActive(request.active());
        if (request.amenities() != null) {
            unit.setAmenities(new LinkedHashSet<>(request.amenities()));
        }
        return repository.save(unit);
    }

    /**
     * "La disponibilidad disminuye al confirmar la reserva" (caso, seccion 3). Se llama
     * desde ms-andesstay-reservations al pasar una reserva a CONFIRMADA; si la unidad ya
     * esta al limite, se rechaza aqui para no permitir sobreventa (overbooking).
     */
    @Transactional
    public CatalogUnit reserveUnit(Long id) {
        int updated = repository.decrementAvailability(id, Instant.now());
        if (updated == 0) {
            if (!repository.existsById(id)) {
                throw new CatalogUnitNotFoundException(id);
            }
            throw new NoAvailabilityException(id);
        }
        return findById(id);
    }

    /**
     * Complemento de reserveUnit: libera la unidad cuando la reserva que la ocupaba
     * termina su ciclo (checkout) o se cancela estando ya confirmada. No falla si la
     * unidad ya esta al maximo de su inventario (operacion idempotente/best-effort).
     */
    @Transactional
    public CatalogUnit releaseUnit(Long id) {
        repository.incrementAvailability(id, Instant.now());
        return findById(id);
    }
}
