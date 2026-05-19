package com.haufe.beercatalogue.service;

import com.haufe.beercatalogue.model.Manufacturer;
import com.haufe.beercatalogue.repository.ManufacturerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;

    @Transactional(readOnly = true)
    public List<Manufacturer> getAllManufacturers() {
        return manufacturerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Manufacturer> getManufacturerById(Long id) {
        return manufacturerRepository.findById(id);
    }

    @Transactional
    public Manufacturer createManufacturer(Manufacturer manufacturer) {
        return manufacturerRepository.save(manufacturer);
    }

    @Transactional
    public Manufacturer updateManufacturer(Long id, Manufacturer updatedManufacturer) {
        return manufacturerRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedManufacturer.getName());
                    existing.setCountry(updatedManufacturer.getCountry());
                    return manufacturerRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Manufacturer not found with id: " + id));
    }

    @Transactional
    public void deleteManufacturer(Long id) {
        if (!manufacturerRepository.existsById(id)) {
            throw new RuntimeException("Manufacturer not found with id: " + id);
        }
        manufacturerRepository.deleteById(id);
    }
}
