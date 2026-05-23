package com.haufe.beercatalogue.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haufe.beercatalogue.dto.request.ManufacturerRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ManufacturerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
}