package com.haufe.beercatalogue.controller;

import com.haufe.beercatalogue.dto.BeerSearchCriteria;
import com.haufe.beercatalogue.dto.request.BeerRequest;
import com.haufe.beercatalogue.dto.response.BeerResponse;
import com.haufe.beercatalogue.mapper.BeerMapper;
import com.haufe.beercatalogue.model.Beer;
import com.haufe.beercatalogue.service.BeerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/beers")
@RequiredArgsConstructor
public class BeerController {

    private final BeerService beerService;
    private final BeerMapper beerMapper;

    @GetMapping
    public ResponseEntity<Page<BeerResponse>> getAllBeers(BeerSearchCriteria criteria, Pageable pageable) {
        return ResponseEntity.ok(beerService.getAllBeers(criteria, pageable).map(beerMapper::toResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeerResponse> getBeerById(@PathVariable Long id) {
        return ResponseEntity.ok(beerMapper.toResponse(beerService.getBeerById(id)));
    }

    @GetMapping("/manufacturer/{manufacturerId}")
    public ResponseEntity<Page<BeerResponse>> getBeersByManufacturer(@PathVariable Long manufacturerId, Pageable pageable) {
        return ResponseEntity.ok(beerService.getBeersByManufacturer(manufacturerId, pageable).map(beerMapper::toResponse));
    }

    @PostMapping
    public ResponseEntity<BeerResponse> createBeer(@Valid @RequestBody BeerRequest request) {
        Beer saved = beerService.createBeer(beerMapper.toEntity(request), request.manufacturerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(beerMapper.toResponse(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeerResponse> updateBeer(
            @PathVariable Long id,
            @Valid @RequestBody BeerRequest request) {
        Beer updated = beerService.updateBeer(id, beerMapper.toEntity(request), request.manufacturerId());
        return ResponseEntity.ok(beerMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeer(@PathVariable Long id) {
        beerService.deleteBeer(id);
        return ResponseEntity.noContent().build();
    }
}