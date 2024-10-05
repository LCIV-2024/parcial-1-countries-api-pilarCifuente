package ar.edu.utn.frc.tup.lciii.service;

import ar.edu.utn.frc.tup.lciii.dtos.common.CountryDTO;
import ar.edu.utn.frc.tup.lciii.dtos.common.CountryNameDTO;
import ar.edu.utn.frc.tup.lciii.entity.CountryEntity;
import ar.edu.utn.frc.tup.lciii.model.Country;
import ar.edu.utn.frc.tup.lciii.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountryService {

        private final CountryRepository countryRepository;

        private final RestTemplate restTemplate;

        public List<Country> getAllCountries() {
                String url = "https://restcountries.com/v3.1/all";
                List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);
                return response.stream().map(this::mapToCountry).collect(Collectors.toList());
        }

        /**
         * Agregar mapeo de campo cca3 (String)
         * Agregar mapeo campos borders ((List<String>))
         */
        private Country mapToCountry(Map<String, Object> countryData) {
                Map<String, Object> nameData = (Map<String, Object>) countryData.get("name");
                List<String> borders = (List<String>) countryData.get("borders");
                return Country.builder()
                        .name((String) nameData.get("common"))
                        .code((String) countryData.get("cca3"))
                        .population(((Number) countryData.get("population")).longValue())
                        .area(((Number) countryData.get("area")).doubleValue())
                        .region((String) countryData.get("region"))
                        .borders(borders)
                        .languages((Map<String, String>) countryData.get("languages"))
                        .build();
        }



        public CountryDTO mapToDTO(Country country) {
                return new CountryDTO(country.getCode(), country.getName());
        }

        public List<CountryDTO> getAllCountriesDTO() {
                List<CountryDTO> countriesDTO = new ArrayList<>();
                List<Country> countries = getAllCountries();
                for(Country c : countries) {
                        CountryDTO country = mapToDTO(c);
                        countriesDTO.add(country);
                }
                return countriesDTO;
        }

        public CountryDTO obtenerCountryPorNombre(String country_nom) {
                if (country_nom == null || country_nom.trim().isEmpty()) {
                        throw new IllegalArgumentException("El nombre del país no puede ser nulo o vacío");
                }
                List<Country> countries = getAllCountries();
                CountryDTO countryDTO = null;
                for (Country c : countries) {
                        if (c.getName().equalsIgnoreCase(country_nom)) {
                                countryDTO = mapToDTO(c);
                                break;
                        }
                }
                return countryDTO;
        }

        public CountryDTO obtenerCountryPorCodigo(String country_code){
                List<Country> countries = getAllCountries();
                CountryDTO countryDTO = null;
                for (Country c : countries){
                        if (c.getCode().equals(country_code)){
                                countryDTO = mapToDTO(c);
                                break;
                        }
                }
                return countryDTO;
        }


        public List<CountryNameDTO> ObtenerPaisesPorContinente(String continente_nom){
                //arreglar error 500
                List<Country> countries = getAllCountries();
                List<CountryNameDTO> paises = null;
                for(Country c : countries){
                        if (c.getRegion().equals(continente_nom)){
                                CountryNameDTO pais = new CountryNameDTO(c.getName());
                                paises.add(pais);
                        }
                }
                return paises;
        }

        public List<CountryNameDTO> ObtenerPaisesPorLenguaje(String lenguaje) {
                //arreglar error 500
                List<Country> countries = getAllCountries();
                List<CountryNameDTO> paises = new ArrayList<>();

                if (lenguaje != null) {
                        for (Country c : countries) {
                                if (c.getLanguages() != null && c.getLanguages().containsKey(lenguaje)) {
                                        CountryNameDTO pais = new CountryNameDTO(c.getName());
                                        paises.add(pais);
                                }
                        }
                }
                return paises;
        }




        public CountryDTO obtenerCountryMasBorders() {
                List<Country> countries = getAllCountries();

                Country countryMasBorders = null;
                int maxBorders = -1;
                for (Country country : countries) {
                        if (country.getBorders() != null && country.getBorders().size() > maxBorders) {
                                maxBorders = country.getBorders().size();
                                countryMasBorders = country;
                        }
                }
                //CountryDTO countryDTO = countryDTO = mapToDTO(countryMasBorders);

                return countryMasBorders != null ? mapToDTO(countryMasBorders) : null;
        }


        public List<CountryDTO> saveRandomCountries(int cantidad) {
                List<Country> countries = getAllCountries();

                Collections.shuffle(countries);
                List<Country> selectedCountries = countries.stream()
                        .limit(cantidad)
                        .collect(Collectors.toList());

                List<CountryEntity> countryEntities = new ArrayList<>();
                for (Country country : selectedCountries) {
                        CountryEntity countryEntity = new CountryEntity();
                        countryEntity.setName(country.getName());
                        countryEntity.setArea(country.getArea());
                        countryEntity.setCode(country.getCode());
                        countryEntity.setPopulation(country.getPopulation());
                        countryEntities.add(countryEntity);
                }

                List<CountryDTO> savedCountriesDTO = new ArrayList<>();
                for (CountryEntity countryEntity : countryEntities) {
                        CountryEntity CountryEntityGuardada = countryRepository.save(countryEntity);

                        CountryDTO countryDTO = new CountryDTO();
                        countryDTO.setCode(CountryEntityGuardada.getCode());
                        countryDTO.setName(CountryEntityGuardada.getName());
                        savedCountriesDTO.add(countryDTO);
                }

                return savedCountriesDTO;
        }


}





