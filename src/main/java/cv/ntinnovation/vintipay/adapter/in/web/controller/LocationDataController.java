package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.out.persistence.repository.CityRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.CountryRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para verificar os dados de localização
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/location-data")
public class LocationDataController {

    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;

    /**
     * Retorna a contagem de países, estados e cidades no banco de dados
     * @return Mapa com as contagens
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getLocationDataCount() {
        Map<String, Long> counts = new HashMap<>();
        counts.put("countries", countryRepository.count());
        counts.put("states", stateRepository.count());
        counts.put("cities", cityRepository.count());
        return ResponseEntity.ok(counts);
    }
}
