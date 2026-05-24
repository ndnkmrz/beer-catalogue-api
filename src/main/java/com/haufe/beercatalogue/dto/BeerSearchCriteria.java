package com.haufe.beercatalogue.dto;

import com.haufe.beercatalogue.model.BeerType;

public record BeerSearchCriteria(
        String name,
        BeerType type,
        Double minAbv,
        Double maxAbv
) {}