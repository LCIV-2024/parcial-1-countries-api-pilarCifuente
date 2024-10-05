package ar.edu.utn.frc.tup.lciii.controllers;
import ar.edu.utn.frc.tup.lciii.dtos.common.CountryDTO;
import ar.edu.utn.frc.tup.lciii.dtos.common.CountryNameDTO;
import ar.edu.utn.frc.tup.lciii.dtos.common.CountryRequestDTO;
import ar.edu.utn.frc.tup.lciii.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CountryController {

    private final CountryService countryService;

    @GetMapping("/countries")
    public ResponseEntity<List<CountryDTO>> getCountries(@RequestParam(required = false) String name,
                                                         @RequestParam(required = false) String code) {

        List<CountryDTO> countries = null;

        if (name == null && code == null){
            countries = countryService.getAllCountriesDTO();
        }
        if (name != null && code == null){
            CountryDTO countryDTO = countryService.obtenerCountryPorNombre(name);
            countries = new ArrayList<>();
            countries.add(countryDTO);

        }
        if (code != null && name == null){
            CountryDTO countryDTO = countryService.obtenerCountryPorCodigo(code);
            countries = new ArrayList<>();
            countries.add(countryDTO);
        }

        return ResponseEntity.ok(countries);

    }

    @GetMapping("/countries/{continent}/continent")
    public ResponseEntity<List<CountryNameDTO>> getCountries(@PathVariable String continent) {
        List<CountryNameDTO> paises = countryService.ObtenerPaisesPorContinente(continent);
        return ResponseEntity.ok(paises);
    }

    @GetMapping("/countries/{language}/language")
    public List<CountryNameDTO> getCountriesByLanguage(@PathVariable String language) {
        return countryService.ObtenerPaisesPorLenguaje(language);
    }

    @GetMapping("/countries/most-borders")
    public CountryDTO getCountriesMasBorders() {
        CountryDTO countryDTO = countryService.obtenerCountryMasBorders();
        return ResponseEntity.ok(countryDTO).getBody();

    }

    @PostMapping("/countries")
    public ResponseEntity<List<CountryDTO>> saveCountries(@RequestBody CountryRequestDTO request) {
        if (request.getAmountOfCountryToSave() < 1 || request.getAmountOfCountryToSave() > 10) {
            return ResponseEntity.badRequest().build();
        }

        List<CountryDTO> savedCountries = countryService.saveRandomCountries(request.getAmountOfCountryToSave());

        return ResponseEntity.ok(savedCountries);
    }

}