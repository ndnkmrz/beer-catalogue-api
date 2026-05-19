package com.haufe.beercatalogue.repository;

import com.haufe.beercatalogue.model.Beer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BeerRepository extends JpaRepository<Beer, Long> {
    List<Beer> findByManufacturerId(Long manufacturerId);
}
