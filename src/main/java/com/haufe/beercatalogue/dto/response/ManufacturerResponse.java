package com.haufe.beercatalogue.dto.response;

public record ManufacturerResponse(
        Long id,
        String name,
        String country
) {
}