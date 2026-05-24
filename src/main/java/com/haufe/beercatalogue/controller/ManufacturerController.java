package com.haufe.beercatalogue.controller;

import com.haufe.beercatalogue.dto.request.ManufacturerRequest;
import com.haufe.beercatalogue.dto.response.ManufacturerResponse;
import com.haufe.beercatalogue.mapper.ManufacturerMapper;
import com.haufe.beercatalogue.model.Manufacturer;
import com.haufe.beercatalogue.service.ManufacturerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/manufacturers")
@RequiredArgsConstructor
public class ManufacturerController {

    private final ManufacturerService manufacturerService;
    private final ManufacturerMapper manufacturerMapper;

    @GetMapping
    public ResponseEntity<Page<ManufacturerResponse>> getAllManufacturers(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(manufacturerService.getAllManufacturers(pageable).map(manufacturerMapper::toResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ManufacturerResponse> getManufacturerById(@PathVariable Long id) {
        return ResponseEntity.ok(manufacturerMapper.toResponse(manufacturerService.getManufacturerById(id)));
    }

    @PostMapping
    public ResponseEntity<ManufacturerResponse> createManufacturer(@Valid @RequestBody ManufacturerRequest request) {
        Manufacturer saved = manufacturerService.createManufacturer(manufacturerMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(manufacturerMapper.toResponse(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ManufacturerResponse> updateManufacturer(
            @PathVariable Long id,
            @Valid @RequestBody ManufacturerRequest request) {
        Manufacturer updated = manufacturerService.updateManufacturer(id, manufacturerMapper.toEntity(request));
        return ResponseEntity.ok(manufacturerMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteManufacturer(@PathVariable Long id) {
        manufacturerService.deleteManufacturer(id);
        return ResponseEntity.noContent().build();
    }
}