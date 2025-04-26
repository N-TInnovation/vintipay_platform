package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.out.location.City;
import cv.ntinnovation.vintipay.adapter.out.location.Country;
import cv.ntinnovation.vintipay.adapter.out.location.LocationService;
import cv.ntinnovation.vintipay.adapter.out.location.State;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador para fornecer dados de localização (países, estados, cidades)
 */
@Slf4j
@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    /**
     * Obtém a lista de todos os países
     * @return Lista de países
     */
    @GetMapping("/countries")
    public ResponseEntity<List<Country>> getAllCountries() {
        log.debug("Obtendo lista de países");
        List<Country> countries = locationService.getAllCountries();
        return ResponseEntity.ok(countries);
    }

    /**
     * Obtém a lista de estados de um país
     * @param countryCode Código ISO2 do país
     * @return Lista de estados
     */
    @GetMapping("/countries/{countryCode}/states")
    public ResponseEntity<List<State>> getStatesByCountry(@PathVariable String countryCode) {
        log.debug("Obtendo lista de estados para o país {}", countryCode);
        List<State> states = locationService.getStatesByCountry(countryCode);
        return ResponseEntity.ok(states);
    }

    /**
     * Obtém a lista de cidades de um estado
     * @param countryCode Código ISO2 do país
     * @param stateCode Código do estado
     * @return Lista de cidades
     */
    @GetMapping("/countries/{countryCode}/states/{stateCode}/cities")
    public ResponseEntity<List<City>> getCitiesByState(
            @PathVariable String countryCode,
            @PathVariable String stateCode) {
        log.debug("Obtendo lista de cidades para o país {} e estado {}", countryCode, stateCode);
        List<City> cities = locationService.getCitiesByState(countryCode, stateCode);
        return ResponseEntity.ok(cities);
    }
}
