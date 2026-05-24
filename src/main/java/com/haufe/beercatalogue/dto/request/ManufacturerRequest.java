package com.haufe.beercatalogue.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ManufacturerRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be under 100 characters")
        String name,

        @NotBlank(message = "Country is required")
        @Size(max = 50, message = "Country must be under 50 characters")
        String country
) {
}