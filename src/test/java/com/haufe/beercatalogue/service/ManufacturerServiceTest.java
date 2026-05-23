package com.haufe.beercatalogue.service;

import com.haufe.beercatalogue.exception.ResourceNotFoundException;
import com.haufe.beercatalogue.model.Manufacturer;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManufacturerServiceTest {

    @Mock
    private ManufacturerRepository manufacturerRepository;

    @InjectMocks
    private ManufacturerService manufacturerService;

    private Manufacturer mockManufacturer;

    @BeforeEach
    void setUp() {
        mockManufacturer = new Manufacturer();
        mockManufacturer.setId(1L);
        mockManufacturer.setName("Guinness");
        mockManufacturer.setCountry("Ireland");
    }

    @Test
    void shouldReturnAllManufacturers() {
        when(manufacturerRepository.findAll()).thenReturn(List.of(mockManufacturer));

        List<Manufacturer> result = manufacturerService.getAllManufacturers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Guinness", result.getFirst().getName());
        verify(manufacturerRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnManufacturerByIdWhenExists() {
        when(manufacturerRepository.findById(1L)).thenReturn(Optional.of(mockManufacturer));

        Optional<Manufacturer> result = manufacturerService.getManufacturerById(1L);

        assertTrue(result.isPresent());
        assertEquals("Ireland", result.get().getCountry());
        verify(manufacturerRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentManufacturer() {
        when(manufacturerRepository.existsById(99L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> manufacturerService.deleteManufacturer(99L)
        );

        assertEquals("Manufacturer not found with id: 99", exception.getMessage());

        verify(manufacturerRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldCreateManufacturer() {
        when(manufacturerRepository.save(any(Manufacturer.class))).thenReturn(mockManufacturer);

        Manufacturer result = manufacturerService.createManufacturer(mockManufacturer);

        assertNotNull(result);
        assertEquals("Guinness", result.getName());
        verify(manufacturerRepository, times(1)).save(mockManufacturer);
    }

    @Test
    void shouldUpdateManufacturerWhenExists() {
        Manufacturer updatedInfo = new Manufacturer();
        updatedInfo.setName("New Name");
        updatedInfo.setCountry("New Country");

        when(manufacturerRepository.findById(1L)).thenReturn(Optional.of(mockManufacturer));
        when(manufacturerRepository.save(any(Manufacturer.class))).thenReturn(mockManufacturer);

        Manufacturer result = manufacturerService.updateManufacturer(1L, updatedInfo);

        assertEquals("New Name", result.getName());
        assertEquals("New Country", result.getCountry());
        verify(manufacturerRepository, times(1)).findById(1L);
        verify(manufacturerRepository, times(1)).save(mockManufacturer);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentManufacturer() {
        Manufacturer updatedInfo = new Manufacturer();
        updatedInfo.setName("New Name");

        when(manufacturerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> manufacturerService.updateManufacturer(99L, updatedInfo)
        );

        verify(manufacturerRepository, never()).save(any());
    }

    @Test
    void shouldDeleteManufacturerWhenExists() {
        when(manufacturerRepository.existsById(1L)).thenReturn(true);

        manufacturerService.deleteManufacturer(1L);

        verify(manufacturerRepository, times(1)).deleteById(1L);
    }
}