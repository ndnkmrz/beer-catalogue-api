package com.haufe.beercatalogue.controller;

import com.haufe.beercatalogue.dto.request.ManufacturerRequest;
import com.haufe.beercatalogue.dto.response.ManufacturerResponse;
import com.haufe.beercatalogue.exception.ResourceNotFoundException;
import com.haufe.beercatalogue.mapper.ManufacturerMapper;
import com.haufe.beercatalogue.model.Manufacturer;
import com.haufe.beercatalogue.service.ManufacturerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/manufacturers")
@RequiredArgsConstructor
public class ManufacturerController {

    private final ManufacturerService manufacturerService;
    private final ManufacturerMapper manufacturerMapper;

    @GetMapping
    public ResponseEntity<List<ManufacturerResponse>> getAllManufacturers() {
        List<ManufacturerResponse> responses = manufacturerService.getAllManufacturers().stream()
                .map(manufacturerMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ManufacturerResponse> getManufacturerById(@PathVariable Long id) {
        return manufacturerService.getManufacturerById(id)
                .map(manufacturerMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Manufacturer not found with id: " + id));
    }

    @PostMapping
    public ResponseEntity<ManufacturerResponse> createManufacturer(@Valid @RequestBody ManufacturerRequest request) {
        Manufacturer manufacturerToSave = manufacturerMapper.toEntity(request);
        Manufacturer savedManufacturer = manufacturerService.createManufacturer(manufacturerToSave);
        return ResponseEntity.status(HttpStatus.CREATED).body(manufacturerMapper.toResponse(savedManufacturer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ManufacturerResponse> updateManufacturer(
            @PathVariable Long id,
            @Valid @RequestBody ManufacturerRequest request) {
        Manufacturer manufacturerToUpdate = manufacturerMapper.toEntity(request);
        Manufacturer updatedManufacturer = manufacturerService.updateManufacturer(id, manufacturerToUpdate);
        return ResponseEntity.ok(manufacturerMapper.toResponse(updatedManufacturer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteManufacturer(@PathVariable Long id) {
        manufacturerService.deleteManufacturer(id);
        return ResponseEntity.noContent().build();
    }
}