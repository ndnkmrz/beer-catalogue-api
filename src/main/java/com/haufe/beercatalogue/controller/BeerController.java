package com.haufe.beercatalogue.controller;

import com.haufe.beercatalogue.dto.request.BeerRequest;
import com.haufe.beercatalogue.dto.response.BeerResponse;
import com.haufe.beercatalogue.exception.ResourceNotFoundException;
import com.haufe.beercatalogue.mapper.BeerMapper;
import com.haufe.beercatalogue.model.Beer;
import com.haufe.beercatalogue.service.BeerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/beers")
@RequiredArgsConstructor
public class BeerController {

    private final BeerService beerService;
    private final BeerMapper beerMapper;

    @GetMapping
    public ResponseEntity<List<BeerResponse>> getAllBeers() {
        List<BeerResponse> responses = beerService.getAllBeers().stream()
                .map(beerMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeerResponse> getBeerById(@PathVariable Long id) {
        return beerService.getBeerById(id)
                .map(beerMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Beer not found with id: " + id));
    }

    @GetMapping("/manufacturer/{manufacturerId}")
    public ResponseEntity<List<BeerResponse>> getBeersByManufacturer(@PathVariable Long manufacturerId) {
        List<BeerResponse> responses = beerService.getBeersByManufacturer(manufacturerId).stream()
                .map(beerMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<BeerResponse> createBeer(@Valid @RequestBody BeerRequest request) {
        Beer beerToSave = beerMapper.toEntity(request);
        Beer savedBeer = beerService.createBeer(beerToSave, request.manufacturerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(beerMapper.toResponse(savedBeer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeerResponse> updateBeer(
            @PathVariable Long id,
            @Valid @RequestBody BeerRequest request) {
        Beer beerToUpdate = beerMapper.toEntity(request);
        Beer updatedBeer = beerService.updateBeer(id, beerToUpdate, request.manufacturerId());
        return ResponseEntity.ok(beerMapper.toResponse(updatedBeer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeer(@PathVariable Long id) {
        beerService.deleteBeer(id);
        return ResponseEntity.noContent().build();
    }
}