package com.haufe.beercatalogue.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haufe.beercatalogue.dto.request.BeerRequest;
import com.haufe.beercatalogue.model.BeerType;
import com.haufe.beercatalogue.model.Manufacturer;
import com.haufe.beercatalogue.repository.BeerRepository;
import com.haufe.beercatalogue.repository.ManufacturerRepository;
import org.junit.jupiter.api.BeforeEach;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Autowired
    private BeerRepository beerRepository;

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

    @Test
    void shouldGetAllBeers() throws Exception {
        com.haufe.beercatalogue.model.Beer beer = new com.haufe.beercatalogue.model.Beer();
        beer.setName("Test Beer");
        beer.setAbv(5.0);
        beer.setType(BeerType.LAGER);
        beer.setManufacturer(savedManufacturer);
        beerRepository.save(beer);

        mockMvc.perform(get("/api/v1/beers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)));
    }

    @Test
    void shouldGetBeerById() throws Exception {
        com.haufe.beercatalogue.model.Beer beer = new com.haufe.beercatalogue.model.Beer();
        beer.setName("Find Me");
        beer.setAbv(4.5);
        beer.setType(BeerType.ALE);
        beer.setManufacturer(savedManufacturer);
        com.haufe.beercatalogue.model.Beer savedBeer = beerRepository.save(beer);

        mockMvc.perform(get("/api/v1/beers/" + savedBeer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Find Me"));
    }

    @Test
    void shouldReturn404WhenBeerNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/beers/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Beer not found with id: 9999"));
    }

    @Test
    void shouldGetBeersByManufacturer() throws Exception {
        com.haufe.beercatalogue.model.Beer beer = new com.haufe.beercatalogue.model.Beer();
        beer.setName("Manu Beer");
        beer.setAbv(6.0);
        beer.setType(BeerType.IPA);
        beer.setManufacturer(savedManufacturer);
        beerRepository.save(beer);

        mockMvc.perform(get("/api/v1/beers/manufacturer/" + savedManufacturer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.content[0].name").value("Manu Beer"));
    }

    @Test
    void shouldUpdateBeer() throws Exception {
        com.haufe.beercatalogue.model.Beer beer = new com.haufe.beercatalogue.model.Beer();
        beer.setName("Old Beer");
        beer.setAbv(4.0);
        beer.setType(BeerType.IPA);
        beer.setManufacturer(savedManufacturer);
        com.haufe.beercatalogue.model.Beer savedBeer = beerRepository.save(beer);

        BeerRequest updateRequest = new BeerRequest(
                "Updated Beer",
                5.5,
                "Updated description",
                BeerType.IPA,
                savedManufacturer.getId()
        );

        mockMvc.perform(put("/api/v1/beers/" + savedBeer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Beer"))
                .andExpect(jsonPath("$.abv").value(5.5));
    }

    @Test
    void shouldDeleteBeer() throws Exception {
        com.haufe.beercatalogue.model.Beer beer = new com.haufe.beercatalogue.model.Beer();
        beer.setName("To Delete");
        beer.setAbv(5.0);
        beer.setType(BeerType.LAGER);
        beer.setManufacturer(savedManufacturer);
        com.haufe.beercatalogue.model.Beer savedBeer = beerRepository.save(beer);

        mockMvc.perform(delete("/api/v1/beers/" + savedBeer.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldFilterBeers() throws Exception {
        com.haufe.beercatalogue.model.Beer beer = new com.haufe.beercatalogue.model.Beer();
        beer.setName("Dark Stout");
        beer.setAbv(8.0);
        beer.setType(BeerType.STOUT);
        beer.setManufacturer(savedManufacturer);
        beerRepository.save(beer);

        mockMvc.perform(get("/api/v1/beers?name=dark&type=STOUT&minAbv=5.0&maxAbv=10.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Dark Stout"));
    }

    @Test
    void shouldFilterBeersByManufacturer() throws Exception {
        com.haufe.beercatalogue.model.Beer guinnessBeer = new com.haufe.beercatalogue.model.Beer();
        guinnessBeer.setName("Guinness Stout");
        guinnessBeer.setAbv(4.2);
        guinnessBeer.setType(BeerType.STOUT);
        guinnessBeer.setManufacturer(savedManufacturer);
        beerRepository.save(guinnessBeer);

        Manufacturer otherManufacturer = new Manufacturer();
        otherManufacturer.setName("BrewDog");
        otherManufacturer.setCountry("Scotland");
        Manufacturer savedOther = manufacturerRepository.save(otherManufacturer);

        com.haufe.beercatalogue.model.Beer brewDogBeer = new com.haufe.beercatalogue.model.Beer();
        brewDogBeer.setName("Punk IPA");
        brewDogBeer.setAbv(5.4);
        brewDogBeer.setType(BeerType.IPA);
        brewDogBeer.setManufacturer(savedOther);
        beerRepository.save(brewDogBeer);

        mockMvc.perform(get("/api/v1/beers?manufacturerId=" + savedManufacturer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Guinness Stout"))
                .andExpect(jsonPath("$.content[0].manufacturer.name").value("Guinness"));
    }

    @Test
    void shouldReturnBeersSortedByAbvDescending() throws Exception {
        com.haufe.beercatalogue.model.Beer lightBeer = new com.haufe.beercatalogue.model.Beer();
        lightBeer.setName("Light Beer");
        lightBeer.setAbv(3.0);
        lightBeer.setType(BeerType.LAGER);
        lightBeer.setManufacturer(savedManufacturer);

        com.haufe.beercatalogue.model.Beer strongBeer = new com.haufe.beercatalogue.model.Beer();
        strongBeer.setName("Strong Beer");
        strongBeer.setAbv(8.0);
        strongBeer.setType(BeerType.STOUT);
        strongBeer.setManufacturer(savedManufacturer);

        com.haufe.beercatalogue.model.Beer mediumBeer = new com.haufe.beercatalogue.model.Beer();
        mediumBeer.setName("Medium Beer");
        mediumBeer.setAbv(5.0);
        mediumBeer.setType(BeerType.ALE);
        mediumBeer.setManufacturer(savedManufacturer);

        beerRepository.saveAll(java.util.List.of(lightBeer, strongBeer, mediumBeer));

        mockMvc.perform(get("/api/v1/beers?sort=abv,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(3)))
                .andExpect(jsonPath("$.content[0].abv").value(8.0)) // Первым должно быть самое крепкое
                .andExpect(jsonPath("$.content[1].abv").value(5.0))
                .andExpect(jsonPath("$.content[2].abv").value(3.0));
    }
}