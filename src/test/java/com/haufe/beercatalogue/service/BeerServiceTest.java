package com.haufe.beercatalogue.service;

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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        assertEquals(mockManufacturer, savedBeer.getManufacturer());

        verify(manufacturerRepository, times(1)).findById(1L);
        verify(beerRepository, times(1)).save(mockBeer);
    }

    @Test
    void shouldThrowExceptionWhenCreatingBeerWithInvalidManufacturer() {
        when(manufacturerRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> beerService.createBeer(mockBeer, 99L)
        );

        assertEquals("Manufacturer not found with id: 99", exception.getMessage());

        verify(beerRepository, never()).save(any());
    }

    @Test
    void shouldReturnAllBeers() {
        when(beerRepository.findAll()).thenReturn(List.of(mockBeer));

        List<Beer> beers = beerService.getAllBeers();

        assertEquals(1, beers.size());
        verify(beerRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnBeerById() {
        when(beerRepository.findById(1L)).thenReturn(Optional.of(mockBeer));

        Optional<Beer> beer = beerService.getBeerById(1L);

        assertTrue(beer.isPresent());
        assertEquals("Guinness Draught", beer.get().getName());
        verify(beerRepository, times(1)).findById(1L);
    }

    @Test
    void shouldReturnBeersByManufacturer() {
        when(beerRepository.findByManufacturerId(1L)).thenReturn(List.of(mockBeer));

        List<Beer> beers = beerService.getBeersByManufacturer(1L);

        assertEquals(1, beers.size());
        verify(beerRepository, times(1)).findByManufacturerId(1L);
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

        verify(beerRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldUpdateBeerWhenManufacturerStaysTheSame() {
        Beer updatedInfo = new Beer();
        updatedInfo.setName("New Name");
        updatedInfo.setAbv(5.0);

        when(beerRepository.findById(1L)).thenReturn(Optional.of(mockBeer));
        when(beerRepository.save(any(Beer.class))).thenReturn(mockBeer);

        Beer result = beerService.updateBeer(1L, updatedInfo, 1L);

        assertEquals("New Name", result.getName());
        verify(manufacturerRepository, never()).findById(anyLong());
    }

    @Test
    void shouldUpdateBeerWhenManufacturerChanges() {
        Beer updatedInfo = new Beer();
        updatedInfo.setName("New Name");

        Manufacturer newManufacturer = new Manufacturer();
        newManufacturer.setId(2L);
        newManufacturer.setName("New Brewery");

        when(beerRepository.findById(1L)).thenReturn(Optional.of(mockBeer));
        when(manufacturerRepository.findById(2L)).thenReturn(Optional.of(newManufacturer));
        when(beerRepository.save(any(Beer.class))).thenReturn(mockBeer);

        Beer result = beerService.updateBeer(1L, updatedInfo, 2L);

        assertEquals(newManufacturer, result.getManufacturer());
        verify(manufacturerRepository, times(1)).findById(2L);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentBeer() {
        when(beerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> beerService.updateBeer(99L, mockBeer, 1L));

        verify(beerRepository, never()).save(any());
    }
}