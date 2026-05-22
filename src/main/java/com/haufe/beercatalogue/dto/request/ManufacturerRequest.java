package com.haufe.beercatalogue.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ManufacturerRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Name cannot exceed 255 characters")
        String name,

        @NotBlank(message = "Country is required")
        @Size(max = 255, message = "Country cannot exceed 255 characters")
        String country
) {
}