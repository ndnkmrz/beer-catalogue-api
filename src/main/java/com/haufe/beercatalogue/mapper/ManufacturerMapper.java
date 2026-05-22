package com.haufe.beercatalogue.mapper;

import com.haufe.beercatalogue.dto.request.ManufacturerRequest;
import com.haufe.beercatalogue.dto.response.ManufacturerResponse;
import com.haufe.beercatalogue.model.Manufacturer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ManufacturerMapper {

    ManufacturerResponse toResponse(Manufacturer manufacturer);

    @Mapping(target = "id", ignore = true)
    Manufacturer toEntity(ManufacturerRequest request);
}