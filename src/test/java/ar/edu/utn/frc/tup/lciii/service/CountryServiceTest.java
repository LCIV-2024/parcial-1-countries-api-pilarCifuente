package ar.edu.utn.frc.tup.lciii.service;

import ar.edu.utn.frc.tup.lciii.dtos.common.CountryDTO;
import ar.edu.utn.frc.tup.lciii.dtos.common.CountryNameDTO;
import ar.edu.utn.frc.tup.lciii.entity.CountryEntity;
import ar.edu.utn.frc.tup.lciii.model.Country;
import ar.edu.utn.frc.tup.lciii.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CountryService countryService;

    private static final String API_URL = "https://restcountries.com/v3.1/all";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCountries() {
        // Respuesta mock simple
        List<Map<String, Object>> mockResponse = List.of(
                Map.of("name", Map.of("common", "France"), "cca3", "FRA"),
                Map.of("name", Map.of("common", "Brazil"), "cca3", "BRA")
        );

        when(restTemplate.getForObject(API_URL, List.class)).thenReturn(mockResponse);

        List<Country> result = countryService.getAllCountries();

        // Simplificar las aserciones
        assertEquals(2, result.size());
        assertEquals("France", result.get(0).getName());
        assertEquals("FRA", result.get(0).getCode());
    }

    @Test
    void testGetAllCountriesDTO() {
        List<Map<String, Object>> mockResponse = List.of(
                Map.of("name", Map.of("common", "France"), "cca3", "FRA"),
                Map.of("name", Map.of("common", "Brazil"), "cca3", "BRA")
        );

        when(restTemplate.getForObject(API_URL, List.class)).thenReturn(mockResponse);

        List<CountryDTO> result = countryService.getAllCountriesDTO();

        assertEquals(2, result.size());
        assertEquals("FRA", result.get(0).getCode());
        assertEquals("France", result.get(0).getName());
    }

    @Test
    void testObtenerCountryPorNombreNotFound() {
        List<Map<String, Object>> mockResponse = List.of(
                Map.of("name", Map.of("common", "France"), "cca3", "FRA")
        );

        when(restTemplate.getForObject(API_URL, List.class)).thenReturn(mockResponse);

        CountryDTO result = countryService.obtenerCountryPorNombre("NonExistentCountry");

        assertNull(result);
    }

    @Test
    void testObtenerPaisesPorLenguaje() {
        List<Map<String, Object>> mockResponse = List.of(
                Map.of("name", Map.of("common", "France"), "cca3", "FRA", "languages", Map.of("fra", "French"))
        );

        when(restTemplate.getForObject(API_URL, List.class)).thenReturn(mockResponse);

        List<CountryNameDTO> result = countryService.ObtenerPaisesPorLenguaje("fra");

        assertEquals(1, result.size());
        assertEquals("France", result.get(0).getNombre());
    }




}