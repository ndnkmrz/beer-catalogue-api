package com.haufe.beercatalogue;

import org.springframework.boot.SpringApplication;

public class TestBeerCatalogueApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(BeerCatalogueApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
