package com.haufe.beercatalogue.repository;

import com.haufe.beercatalogue.model.Beer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BeerRepository extends JpaRepository<Beer, Long>, JpaSpecificationExecutor<Beer> {

    @EntityGraph(attributePaths = {"manufacturer"})
    Page<Beer> findByManufacturerId(Long manufacturerId, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"manufacturer"})
    Page<Beer> findAll(Specification<Beer> spec, Pageable pageable);
}