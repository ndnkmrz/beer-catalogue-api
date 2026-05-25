package com.haufe.beercatalogue.service;

import com.haufe.beercatalogue.dto.BeerSearchCriteria;
import com.haufe.beercatalogue.exception.ResourceNotFoundException;
import com.haufe.beercatalogue.model.Beer;
import com.haufe.beercatalogue.model.Manufacturer;
import com.haufe.beercatalogue.repository.BeerRepository;
import com.haufe.beercatalogue.repository.ManufacturerRepository;
import com.haufe.beercatalogue.repository.specification.BeerSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BeerService {

    private final BeerRepository beerRepository;
    private final ManufacturerRepository manufacturerRepository;

    @Transactional(readOnly = true)
    public Page<Beer> getAllBeers(BeerSearchCriteria criteria, Pageable pageable) {
        Specification<Beer> spec = BeerSpecification.filterByCriteria(criteria);
        return beerRepository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public Beer getBeerById(Long id) {
        return beerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beer not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Beer> getBeersByManufacturer(Long manufacturerId, Pageable pageable) {
        if (!manufacturerRepository.existsById(manufacturerId)) {
            throw new ResourceNotFoundException("Manufacturer not found with id: " + manufacturerId);
        }
        return beerRepository.findByManufacturerId(manufacturerId, pageable);
    }

    @Transactional
    public Beer createBeer(Beer beer, Long manufacturerId) {
        Manufacturer manufacturer = manufacturerRepository.findById(manufacturerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manufacturer not found with id: " + manufacturerId));

        beer.setManufacturer(manufacturer);
        return beerRepository.save(beer);
    }

    @Transactional
    public Beer updateBeer(Long id, Beer updatedBeer, Long manufacturerId) {
        Beer existing = getBeerById(id);

        existing.setName(updatedBeer.getName());
        existing.setAbv(updatedBeer.getAbv());
        existing.setType(updatedBeer.getType());
        existing.setDescription(updatedBeer.getDescription());

        if (!existing.getManufacturer().getId().equals(manufacturerId)) {
            Manufacturer newManufacturer = manufacturerRepository.findById(manufacturerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Manufacturer not found with id: " + manufacturerId));
            existing.setManufacturer(newManufacturer);
        }

        return beerRepository.save(existing);
    }

    @Transactional
    public void deleteBeer(Long id) {
        if (!beerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Beer not found with id: " + id);
        }
        beerRepository.deleteById(id);
    }
}