package com.haufe.beercatalogue.dto.response;

import com.haufe.beercatalogue.model.BeerType;

public record BeerResponse(
        Long id,
        String name,
        Double abv,
        String description,
        BeerType type,
        ManufacturerResponse manufacturer
) {
}