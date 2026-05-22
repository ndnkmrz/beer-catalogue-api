package com.haufe.beercatalogue.service;

import com.haufe.beercatalogue.model.Beer;
import com.haufe.beercatalogue.model.Manufacturer;
import com.haufe.beercatalogue.repository.BeerRepository;
import com.haufe.beercatalogue.repository.ManufacturerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BeerService {

    private final BeerRepository beerRepository;
    private final ManufacturerRepository manufacturerRepository;

    @Transactional(readOnly = true)
    public List<Beer> getAllBeers() {
        return beerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Beer> getBeerById(Long id) {
        return beerRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Beer> getBeersByManufacturer(Long manufacturerId) {
        return beerRepository.findByManufacturerId(manufacturerId);
    }

    @Transactional
    public Beer createBeer(Beer beer, Long manufacturerId) {
        Manufacturer manufacturer = manufacturerRepository.findById(manufacturerId)
                .orElseThrow(() -> new RuntimeException("Manufacturer not found with id: " + manufacturerId));

        beer.setManufacturer(manufacturer);
        return beerRepository.save(beer);
    }

    @Transactional
    public Beer updateBeer(Long id, Beer updatedBeer, Long manufacturerId) {
        return beerRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedBeer.getName());
                    existing.setAbv(updatedBeer.getAbv());
                    existing.setType(updatedBeer.getType());
                    existing.setDescription(updatedBeer.getDescription());

                    if (!existing.getManufacturer().getId().equals(manufacturerId)) {
                        Manufacturer newManufacturer = manufacturerRepository.findById(manufacturerId)
                                .orElseThrow(() -> new RuntimeException("Manufacturer not found with id: " + manufacturerId));
                        existing.setManufacturer(newManufacturer);
                    }

                    return beerRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Beer not found with id: " + id));
    }

    @Transactional
    public void deleteBeer(Long id) {
        if (!beerRepository.existsById(id)) {
            throw new RuntimeException("Beer not found with id: " + id);
        }
        beerRepository.deleteById(id);
    }
}