package com.haufe.beercatalogue.dto.request;

import com.haufe.beercatalogue.model.BeerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record BeerRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Name cannot exceed 255 characters")
        String name,

        @NotNull(message = "ABV is required")
        @Positive(message = "ABV must be a positive number")
        Double abv,

        @Size(max = 255, message = "Description cannot exceed 255 characters")
        String description,

        @NotNull(message = "Beer type is required")
        BeerType type,

        @NotNull(message = "Manufacturer ID is required")
        Long manufacturerId
) {
}