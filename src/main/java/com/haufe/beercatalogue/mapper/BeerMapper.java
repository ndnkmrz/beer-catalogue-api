package com.haufe.beercatalogue.mapper;

import com.haufe.beercatalogue.dto.request.BeerRequest;
import com.haufe.beercatalogue.dto.response.BeerResponse;
import com.haufe.beercatalogue.model.Beer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ManufacturerMapper.class})
public interface BeerMapper {

    BeerResponse toResponse(Beer beer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "manufacturer", ignore = true)
    Beer toEntity(BeerRequest request);
}