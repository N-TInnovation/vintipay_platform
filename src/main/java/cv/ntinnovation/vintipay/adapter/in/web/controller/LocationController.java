package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.out.location.City;
import cv.ntinnovation.vintipay.adapter.out.location.Country;
import cv.ntinnovation.vintipay.adapter.out.location.LocationService;
import cv.ntinnovation.vintipay.adapter.out.location.State;
import cv.ntinnovation.vintipay.adapter.out.location.util.LocationDataImporter;
import cv.ntinnovation.vintipay.adapter.out.persistence.dto.LocationOptionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @Autowired
    private org.springframework.context.ApplicationContext applicationContext;

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
     * Obtém a lista de estados de um país pelo código numérico
     * @param numericCode Código numérico do país
     * @return Lista de estados
     */
    @GetMapping("/countries/numeric/{numericCode}/states")
    public ResponseEntity<List<State>> getStatesByCountryNumericCode(@PathVariable String numericCode) {
        log.debug("Obtendo lista de estados para o país com código numérico {}", numericCode);
        List<State> states = locationService.getStatesByCountryNumericCode(numericCode);
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

    /**
     * Obtém a lista de cidades de um estado usando o código numérico do país
     * @param numericCode Código numérico do país
     * @param stateCode Código do estado
     * @return Lista de cidades
     */
    @GetMapping("/countries/numeric/{numericCode}/states/{stateCode}/cities")
    public ResponseEntity<List<City>> getCitiesByStateNumericCode(
            @PathVariable String numericCode,
            @PathVariable String stateCode) {
        log.debug("Obtendo lista de cidades para o país com código numérico {} e estado {}", numericCode, stateCode);
        List<City> cities = locationService.getCitiesByStateNumericCode(numericCode, stateCode);
        return ResponseEntity.ok(cities);
    }

    /**
     * Obtém a lista de ilhas de Cabo Verde
     * @return Lista de ilhas
     */
    @GetMapping("/cape-verde/islands")
    public ResponseEntity<List<String>> getCapeVerdeIslands() {
        log.debug("Obtendo lista de ilhas de Cabo Verde");
        List<String> islands = locationService.getAllCapeVerdeIslands();
        return ResponseEntity.ok(islands);
    }

    /**
     * Obtém a lista de concelhos de uma ilha específica
     * @param island Nome da ilha
     * @return Lista de concelhos
     */
    @GetMapping("/cape-verde/islands/{island}/counties")
    public ResponseEntity<List<String>> getCapeVerdeCountiesByIsland(@PathVariable String island) {
        log.debug("Obtendo lista de concelhos para a ilha {}", island);
        List<String> counties = locationService.getCapeVerdeCountiesByIsland(island);
        return ResponseEntity.ok(counties);
    }

    /**
     * Obtém a lista de freguesias de um concelho específico
     * @param county Nome do concelho
     * @return Lista de freguesias
     */
    @GetMapping("/cape-verde/counties/{county}/parishes")
    public ResponseEntity<List<String>> getCapeVerdeParishesByCounty(@PathVariable String county) {
        log.debug("Obtendo lista de freguesias para o concelho {}", county);
        List<String> parishes = locationService.getCapeVerdeParishesByCounty(county);
        return ResponseEntity.ok(parishes);
    }

    /**
     * Obtém a lista de zonas de uma freguesia específica
     * @param parish Nome da freguesia
     * @return Lista de zonas
     */
    @GetMapping("/cape-verde/parishes/{parish}/zones")
    public ResponseEntity<List<String>> getCapeVerdeZonesByParish(@PathVariable String parish) {
        log.debug("Obtendo lista de zonas para a freguesia {}", parish);
        List<String> zones = locationService.getCapeVerdeZonesByParish(parish);
        return ResponseEntity.ok(zones);
    }

    /**
     * Obtém a lista de subzonas de uma zona específica
     * @param zone Nome da zona
     * @return Lista de subzonas
     */
    @GetMapping("/cape-verde/zones/{zone}/subzones")
    public ResponseEntity<List<String>> getCapeVerdeSubzonesByZone(@PathVariable String zone) {
        log.debug("Obtendo lista de subzonas para a zona {}", zone);
        List<String> subzones = locationService.getCapeVerdeSubzonesByZone(zone);
        return ResponseEntity.ok(subzones);
    }

    /**
     * Obtém a lista de ilhas de Cabo Verde com seus nomes para exibição
     * @return Lista de opções de ilha (valor/label)
     */
    @GetMapping("/cape-verde/islands/with-names")
    public ResponseEntity<List<LocationOptionDTO>> getCapeVerdeIslandsWithNames() {
        log.debug("Obtendo lista de ilhas de Cabo Verde com nomes");
        List<LocationOptionDTO> islands = locationService.getAllCapeVerdeIslandsWithNames();
        return ResponseEntity.ok(islands);
    }

    /**
     * Obtém a lista de concelhos de uma ilha específica com seus nomes para exibição
     * @param island Nome da ilha
     * @return Lista de opções de concelho (valor/label)
     */
    @GetMapping("/cape-verde/islands/{island}/counties/with-names")
    public ResponseEntity<List<LocationOptionDTO>> getCapeVerdeCountiesByIslandWithNames(@PathVariable String island) {
        log.debug("Obtendo lista de concelhos para a ilha {} com nomes", island);
        List<LocationOptionDTO> counties = locationService.getCapeVerdeCountiesByIslandWithNames(island);
        return ResponseEntity.ok(counties);
    }

    /**
     * Obtém a lista de freguesias de um concelho específico com seus nomes para exibição
     * @param county Nome do concelho
     * @return Lista de opções de freguesia (valor/label)
     */
    @GetMapping("/cape-verde/counties/{county}/parishes/with-names")
    public ResponseEntity<List<LocationOptionDTO>> getCapeVerdeParishesByCountyWithNames(@PathVariable String county) {
        log.debug("Obtendo lista de freguesias para o concelho {} com nomes", county);
        List<LocationOptionDTO> parishes = locationService.getCapeVerdeParishesByCountyWithNames(county);
        return ResponseEntity.ok(parishes);
    }

    /**
     * Obtém a lista de zonas de uma freguesia específica com seus nomes para exibição
     * @param parish Nome da freguesia
     * @return Lista de opções de zona (valor/label)
     */
    @GetMapping("/cape-verde/parishes/{parish}/zones/with-names")
    public ResponseEntity<List<LocationOptionDTO>> getCapeVerdeZonesByParishWithNames(@PathVariable String parish) {
        log.debug("Obtendo lista de zonas para a freguesia {} com nomes", parish);
        List<LocationOptionDTO> zones = locationService.getCapeVerdeZonesByParishWithNames(parish);
        return ResponseEntity.ok(zones);
    }

    /**
     * Obtém a lista de subzonas de uma zona específica com seus nomes para exibição
     * @param zone Nome da zona
     * @return Lista de opções de subzona (valor/label)
     */
    @GetMapping("/cape-verde/zones/{zone}/subzones/with-names")
    public ResponseEntity<List<LocationOptionDTO>> getCapeVerdeSubzonesByZoneWithNames(@PathVariable String zone) {
        log.debug("Obtendo lista de subzonas para a zona {} com nomes", zone);
        List<LocationOptionDTO> subzones = locationService.getCapeVerdeSubzonesByZoneWithNames(zone);
        return ResponseEntity.ok(subzones);
    }

    @GetMapping("/cape-verde/read-excel-header")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> readExcelHeader() {
        try {
            // Obter o bean LocationDataImporter para chamar o método de leitura do cabeçalho
            LocationDataImporter importer = applicationContext.getBean(LocationDataImporter.class);
            importer.readExcelHeader();
            return ResponseEntity.ok("Leitura do cabeçalho do arquivo Excel iniciada. Verifique os logs para mais informações.");
        } catch (Exception e) {
            log.error("Erro ao ler cabeçalho do arquivo Excel", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao ler cabeçalho do arquivo Excel: " + e.getMessage());
        }
    }

    @PostMapping("/cape-verde/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> importCapeVerdeLocations() {
        try {
            // Obter o bean LocationDataImporter para chamar o método de importação manual
            LocationDataImporter importer = applicationContext.getBean(LocationDataImporter.class);
            importer.importCapeVerdeLocationsManually();
            return ResponseEntity.ok("Importação de localizações de Cabo Verde iniciada com sucesso!");
        } catch (Exception e) {
            log.error("Erro ao importar localizações de Cabo Verde", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao importar localizações de Cabo Verde: " + e.getMessage());
        }
    }
}
