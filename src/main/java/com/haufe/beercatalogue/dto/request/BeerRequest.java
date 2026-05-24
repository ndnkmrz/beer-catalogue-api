package com.haufe.beercatalogue.dto.request;

import com.haufe.beercatalogue.model.BeerType;
import jakarta.validation.constraints.*;

public record BeerRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be under 100 characters")
        String name,

        @NotNull(message = "ABV is required")
        @PositiveOrZero(message = "ABV cannot be negative")
        @Max(value = 100, message = "ABV cannot be greater than 100%")
        Double abv,

        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        String description,

        @NotNull(message = "Beer type is required")
        BeerType type,

        @NotNull(message = "Manufacturer ID is required")
        Long manufacturerId
) {
}