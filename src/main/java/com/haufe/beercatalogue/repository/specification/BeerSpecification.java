package com.haufe.beercatalogue.repository.specification;

import com.haufe.beercatalogue.dto.BeerSearchCriteria;
import com.haufe.beercatalogue.model.Beer;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class BeerSpecification {
    public static Specification<Beer> filterByCriteria(BeerSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria != null) {
                if (criteria.name() != null && !criteria.name().isBlank()) {
                    predicates.add(cb.like(cb.lower(root.get("name")), "%" + criteria.name().toLowerCase() + "%"));
                }
                if (criteria.type() != null) {
                    predicates.add(cb.equal(root.get("type"), criteria.type()));
                }
                if (criteria.minAbv() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("abv"), criteria.minAbv()));
                }
                if (criteria.maxAbv() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("abv"), criteria.maxAbv()));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}