package com.haufe.beercatalogue.repository.specification;

import com.haufe.beercatalogue.dto.BeerSearchCriteria;
import com.haufe.beercatalogue.model.Beer;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class BeerSpecification {

    private BeerSpecification() {
    }

    public static Specification<Beer> filterByCriteria(BeerSearchCriteria criteria) {
        return (root, query, cb) -> {
            if (criteria == null) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            addNameFilter(predicates, root, cb, criteria.name());
            addEquals(predicates, cb, root.get("type"), criteria.type());
            addMinAbv(predicates, root, cb, criteria.minAbv());
            addMaxAbv(predicates, root, cb, criteria.maxAbv());
            addEquals(predicates, cb, root.get("manufacturer").get("id"), criteria.manufacturerId());

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addNameFilter(List<Predicate> predicates, Root<Beer> root, CriteriaBuilder cb, String name) {
        if (name != null && !name.isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }
    }

    private static void addMinAbv(List<Predicate> predicates, Root<Beer> root, CriteriaBuilder cb, Double minAbv) {
        if (minAbv != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("abv"), minAbv));
        }
    }

    private static void addMaxAbv(List<Predicate> predicates, Root<Beer> root, CriteriaBuilder cb, Double maxAbv) {
        if (maxAbv != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("abv"), maxAbv));
        }
    }

    private static void addEquals(List<Predicate> predicates, CriteriaBuilder cb, jakarta.persistence.criteria.Path<?> path, Object value) {
        if (value != null) {
            predicates.add(cb.equal(path, value));
        }
    }
}