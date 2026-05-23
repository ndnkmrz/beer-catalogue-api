package com.haufe.beercatalogue.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haufe.beercatalogue.dto.request.BeerRequest;
import com.haufe.beercatalogue.model.BeerType;
import com.haufe.beercatalogue.model.Manufacturer;
import com.haufe.beercatalogue.repository.ManufacturerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Testcontainers
class BeerControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    private Manufacturer savedManufacturer;

    @BeforeEach
    void setUp() {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Guinness");
        manufacturer.setCountry("Ireland");
        savedManufacturer = manufacturerRepository.save(manufacturer);
    }

    @Test
    void shouldCreateBeerAndReturn201() throws Exception {
        BeerRequest request = new BeerRequest(
                "Guinness Draught",
                4.2,
                "Classic Irish Stout",
                BeerType.STOUT,
                savedManufacturer.getId()
        );

        mockMvc.perform(post("/api/v1/beers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Guinness Draught"))
                // Проверяем, что MapStruct правильно собрал вложенный объект пивоварни в ответе
                .andExpect(jsonPath("$.manufacturer.name").value("Guinness"));
    }

    @Test
    void shouldReturn404WhenCreatingBeerWithInvalidManufacturer() throws Exception {
        BeerRequest request = new BeerRequest(
                "Ghost Beer",
                5.0,
                "No manufacturer",
                BeerType.LAGER,
                9999L
        );

        mockMvc.perform(post("/api/v1/beers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Manufacturer not found with id: 9999"));
    }
}