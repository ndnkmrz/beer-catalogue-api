package com.haufe.beercatalogue.service;

import com.haufe.beercatalogue.exception.ResourceNotFoundException;
import com.haufe.beercatalogue.model.Manufacturer;
import com.haufe.beercatalogue.repository.ManufacturerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;

    @Transactional(readOnly = true)
    public Page<Manufacturer> getAllManufacturers(Pageable pageable) {
        return manufacturerRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Manufacturer getManufacturerById(Long id) {
        return manufacturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manufacturer not found with id: " + id));
    }

    @Transactional
    public Manufacturer createManufacturer(Manufacturer manufacturer) {
        return manufacturerRepository.save(manufacturer);
    }

    @Transactional
    public Manufacturer updateManufacturer(Long id, Manufacturer updatedManufacturer) {
        Manufacturer existing = getManufacturerById(id);
        existing.setName(updatedManufacturer.getName());
        existing.setCountry(updatedManufacturer.getCountry());
        return manufacturerRepository.save(existing);
    }

    @Transactional
    public void deleteManufacturer(Long id) {
        if (!manufacturerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Manufacturer not found with id: " + id);
        }
        manufacturerRepository.deleteById(id);
    }
}