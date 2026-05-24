package com.haufe.beercatalogue.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haufe.beercatalogue.dto.request.ManufacturerRequest;
import com.haufe.beercatalogue.repository.ManufacturerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Testcontainers
class ManufacturerControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateManufacturerAndReturn201() throws Exception {
        ManufacturerRequest request = new ManufacturerRequest("Guinness", "Ireland");

        mockMvc.perform(post("/api/v1/manufacturers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Guinness"))
                .andExpect(jsonPath("$.country").value("Ireland"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void shouldReturn400WhenNameIsBlank() throws Exception {
        ManufacturerRequest request = new ManufacturerRequest("", "Ireland");

        mockMvc.perform(post("/api/v1/manufacturers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is required"));
    }

    @Test
    void shouldReturn404WhenManufacturerNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/manufacturers/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Manufacturer not found with id: 9999"));
    }

    @Test
    void shouldGetAllManufacturers() throws Exception {
        com.haufe.beercatalogue.model.Manufacturer m = new com.haufe.beercatalogue.model.Manufacturer();
        m.setName("Test Brewery");
        m.setCountry("Test Country");
        manufacturerRepository.save(m);

        mockMvc.perform(get("/api/v1/manufacturers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(greaterThanOrEqualTo(1)));
    }

    @Test
    void shouldGetManufacturerById() throws Exception {
        com.haufe.beercatalogue.model.Manufacturer m = new com.haufe.beercatalogue.model.Manufacturer();
        m.setName("Find Me Brewery");
        m.setCountry("Germany");
        com.haufe.beercatalogue.model.Manufacturer saved = manufacturerRepository.save(m);

        mockMvc.perform(get("/api/v1/manufacturers/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Find Me Brewery"))
                .andExpect(jsonPath("$.country").value("Germany"));
    }

    @Test
    void shouldUpdateManufacturer() throws Exception {
        com.haufe.beercatalogue.model.Manufacturer m = new com.haufe.beercatalogue.model.Manufacturer();
        m.setName("Old Name");
        m.setCountry("Old Country");
        com.haufe.beercatalogue.model.Manufacturer saved = manufacturerRepository.save(m);

        ManufacturerRequest updateRequest = new ManufacturerRequest("New Name", "New Country");

        mockMvc.perform(put("/api/v1/manufacturers/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.country").value("New Country"));
    }

    @Test
    void shouldDeleteManufacturer() throws Exception {
        com.haufe.beercatalogue.model.Manufacturer m = new com.haufe.beercatalogue.model.Manufacturer();
        m.setName("To Delete");
        m.setCountry("To Delete");
        com.haufe.beercatalogue.model.Manufacturer saved = manufacturerRepository.save(m);

        mockMvc.perform(delete("/api/v1/manufacturers/" + saved.getId()))
                .andExpect(status().isNoContent());
    }
}