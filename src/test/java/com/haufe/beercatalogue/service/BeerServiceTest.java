package com.haufe.beercatalogue.service;

import com.haufe.beercatalogue.dto.BeerSearchCriteria;
import com.haufe.beercatalogue.exception.ResourceNotFoundException;
import com.haufe.beercatalogue.model.Beer;
import com.haufe.beercatalogue.model.BeerType;
import com.haufe.beercatalogue.model.Manufacturer;
import com.haufe.beercatalogue.repository.BeerRepository;
import com.haufe.beercatalogue.repository.ManufacturerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeerServiceTest {

    @Mock
    private BeerRepository beerRepository;

    @Mock
    private ManufacturerRepository manufacturerRepository;

    @InjectMocks
    private BeerService beerService;

    private Manufacturer mockManufacturer;
    private Beer mockBeer;

    @BeforeEach
    void setUp() {
        mockManufacturer = new Manufacturer();
        mockManufacturer.setId(1L);
        mockManufacturer.setName("Guinness");

        mockBeer = new Beer();
        mockBeer.setId(1L);
        mockBeer.setName("Guinness Draught");
        mockBeer.setAbv(4.2);
        mockBeer.setType(BeerType.STOUT);
        mockBeer.setManufacturer(mockManufacturer);
    }

    @Test
    void shouldCreateBeerSuccessfullyWhenManufacturerExists() {
        when(manufacturerRepository.findById(1L)).thenReturn(Optional.of(mockManufacturer));
        when(beerRepository.save(any(Beer.class))).thenReturn(mockBeer);

        Beer savedBeer = beerService.createBeer(mockBeer, 1L);

        assertNotNull(savedBeer);
        assertEquals("Guinness Draught", savedBeer.getName());
        verify(manufacturerRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenCreatingBeerWithInvalidManufacturer() {
        when(manufacturerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> beerService.createBeer(mockBeer, 99L));
        verify(beerRepository, never()).save(any());
    }

    @Test
    void shouldReturnAllBeers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Beer> page = new PageImpl<>(List.of(mockBeer));
        when(beerRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<Beer> beers = beerService.getAllBeers(new BeerSearchCriteria(null, null, null, null, null), pageable);

        assertEquals(1, beers.getContent().size());
    }

    @Test
    void shouldReturnBeerById() {
        when(beerRepository.findById(1L)).thenReturn(Optional.of(mockBeer));

        Beer beer = beerService.getBeerById(1L);

        assertNotNull(beer);
        assertEquals("Guinness Draught", beer.getName());
    }

    @Test
    void shouldReturnBeersByManufacturer() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Beer> page = new PageImpl<>(List.of(mockBeer));
        when(manufacturerRepository.existsById(1L)).thenReturn(true);
        when(beerRepository.findByManufacturerId(1L, pageable)).thenReturn(page);

        Page<Beer> beers = beerService.getBeersByManufacturer(1L, pageable);

        assertEquals(1, beers.getContent().size());
    }

    @Test
    void shouldDeleteBeerWhenExists() {
        when(beerRepository.existsById(1L)).thenReturn(true);
        beerService.deleteBeer(1L);
        verify(beerRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentBeer() {
        when(beerRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> beerService.deleteBeer(99L));
    }

    @Test
    void shouldUpdateBeerWhenManufacturerStaysTheSame() {
        when(beerRepository.findById(1L)).thenReturn(Optional.of(mockBeer));
        when(beerRepository.save(any(Beer.class))).thenReturn(mockBeer);

        Beer updatedInfo = new Beer();
        updatedInfo.setName("New Name");

        Beer result = beerService.updateBeer(1L, updatedInfo, 1L);

        assertEquals("New Name", result.getName());
        verify(manufacturerRepository, never()).findById(anyLong());
    }

    @Test
    void shouldUpdateBeerWhenManufacturerChanges() {
        Manufacturer newMan = new Manufacturer();
        newMan.setId(2L);

        when(beerRepository.findById(1L)).thenReturn(Optional.of(mockBeer));
        when(manufacturerRepository.findById(2L)).thenReturn(Optional.of(newMan));
        when(beerRepository.save(any(Beer.class))).thenReturn(mockBeer);

        Beer result = beerService.updateBeer(1L, new Beer(), 2L);

        assertEquals(newMan, result.getManufacturer());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentBeer() {
        when(beerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> beerService.updateBeer(99L, mockBeer, 1L));

        verify(beerRepository, never()).save(any());
    }
}