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
}