package cv.ntinnovation.vintipay.adapter.out.location.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.CapeVerdeLocationsEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.CityEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.CountryEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.StateEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.CapeVerdeLocationsRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.CityRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.CountryRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Classe utilitária para importar dados de localização (países, estados e cidades)
 * a partir de arquivos JSON.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class LocationDataImporter {

    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final ObjectMapper objectMapper;
    private final CapeVerdeLocationsRepository capeVerdeLocationsRepository;

    // Usando caminhos relativos baseados no diretório de trabalho
    private String getJsonDir() {
        // Primeiro tenta encontrar no classpath
        try {
            Resource resource = new ClassPathResource("json");
            if (resource.exists()) {
                return resource.getFile().getAbsolutePath();
            }
        } catch (Exception e) {
            log.debug("Não foi possível encontrar diretório json no classpath", e);
        }
        
        // Se não encontrou no classpath, usa o diretório de trabalho
        return System.getProperty("user.dir") + "/json";
    }
    
    private String getCountriesJsonPath() {
        return getJsonDir() + "/countries.json";
    }
    
    private String getStatesJsonPath() {
        return getJsonDir() + "/states.json";
    }
    
    private String getCitiesJsonPath() {
        return getJsonDir() + "/cities.json";
    }
    
    private String getCvLocationsExcelPath() {
        // Primeiro tenta encontrar no classpath
        try {
            Resource resource = new ClassPathResource("codigo-paises-lugares-cv.xlsx");
            if (resource.exists()) {
                return resource.getFile().getAbsolutePath();
            }
        } catch (Exception e) {
            log.debug("Não foi possível encontrar arquivo Excel no classpath", e);
        }
        
        // Se não encontrou no classpath, usa o diretório de trabalho
        return System.getProperty("user.dir") + "/codigo-paises-lugares-cv.xlsx";
    }

    /**
     * Bean para importar países
     */
    @Bean
    @Order(1)
    public CommandLineRunner importCountries() {
        return args -> {
            log.info("Verificando se é necessário importar países...");
            if (countryRepository.count() == 0) {
                log.info("Iniciando importação de países...");
                try {
                    List<Map<String, Object>> countriesData = objectMapper.readValue(
                            new File(getCountriesJsonPath()),
                            new TypeReference<List<Map<String, Object>>>() {}
                    );
                    
                    List<CountryEntity> countries = new ArrayList<>();
                    
                    for (Map<String, Object> countryData : countriesData) {
                        CountryEntity country = new CountryEntity();
                        country.setName((String) countryData.get("name"));
                        country.setIso3((String) countryData.get("iso3"));
                        country.setIso2((String) countryData.get("iso2"));
                        country.setNumericCode((String) countryData.get("numeric_code"));
                        country.setPhonecode((String) countryData.get("phonecode"));
                        country.setCapital((String) countryData.get("capital"));
                        country.setCurrency((String) countryData.get("currency"));
                        country.setCurrencyName((String) countryData.get("currency_name"));
                        country.setCurrencySymbol((String) countryData.get("currency_symbol"));
                        country.setTld((String) countryData.get("tld"));
                        country.setNativeName((String) countryData.get("native"));
                        country.setRegion((String) countryData.get("region"));
                        country.setSubregion((String) countryData.get("subregion"));
                        country.setLatitude((String) countryData.get("latitude"));
                        country.setLongitude((String) countryData.get("longitude"));
                        country.setEmoji((String) countryData.get("emoji"));
                        country.setEmojiU((String) countryData.get("emojiU"));
                        
                        // Definir flag como true por padrão
                        country.setFlag(true);
                        
                        countries.add(country);
                        
                        // Importar em lotes de 50 para evitar problemas de memória
                        if (countries.size() >= 50) {
                            countryRepository.saveAll(countries);
                            countries.clear();
                        }
                    }
                    
                    // Salvar países restantes
                    if (!countries.isEmpty()) {
                        countryRepository.saveAll(countries);
                    }
                    
                    log.info("Importação de países concluída com sucesso!");
                } catch (IOException e) {
                    log.error("Erro ao importar países", e);
                }
            } else {
                log.info("Países já existem no banco de dados. Pulando importação.");
            }
        };
    }

    /**
     * Bean para importar estados
     */
    @Bean
    @Order(2)
    public CommandLineRunner importStates() {
        return args -> {
            log.info("Verificando se é necessário importar estados...");
            if (stateRepository.count() == 0) {
                log.info("Iniciando importação de estados...");
                try {
                    List<Map<String, Object>> statesData = objectMapper.readValue(
                            new File(getStatesJsonPath()),
                            new TypeReference<List<Map<String, Object>>>() {}
                    );
                    
                    List<StateEntity> states = new ArrayList<>();
                    
                    for (Map<String, Object> stateData : statesData) {
                        String countryCode = (String) stateData.get("country_code");
                        CountryEntity country = countryRepository.findByIso2(countryCode).orElse(null);
                        
                        if (country != null) {
                            StateEntity state = new StateEntity();
                            state.setName((String) stateData.get("name"));
                            state.setCountry(country);
                            state.setCountryCode(countryCode);
                            state.setStateCode((String) stateData.get("state_code"));
                            state.setType((String) stateData.get("type"));
                            
                            // Converter latitude e longitude para BigDecimal se necessário
                            String latitude = (String) stateData.get("latitude");
                            String longitude = (String) stateData.get("longitude");
                            state.setLatitude(latitude);
                            state.setLongitude(longitude);
                            
                            // Definir flag como true por padrão
                            state.setFlag(true);
                            
                            states.add(state);
                            
                            // Importar em lotes de 100 para evitar problemas de memória
                            if (states.size() >= 100) {
                                stateRepository.saveAll(states);
                                states.clear();
                            }
                        }
                    }
                    
                    // Salvar estados restantes
                    if (!states.isEmpty()) {
                        stateRepository.saveAll(states);
                    }
                    
                    log.info("Importação de estados concluída com sucesso!");
                } catch (IOException e) {
                    log.error("Erro ao importar estados", e);
                }
            } else {
                log.info("Estados já existem no banco de dados. Pulando importação.");
            }
        };
    }

    /**
     * Bean para importar cidades
     * Nota: Este método pode ser lento devido ao grande volume de dados.
     * Considere importar apenas as cidades necessárias para sua aplicação.
     */
    @Bean
    @Order(3)
    public CommandLineRunner importCities() {
        return args -> {
            log.info("Verificando se é necessário importar cidades...");
            if (cityRepository.count() == 0) {
                log.info("Iniciando importação de cidades...");
                try {
                    // Importar apenas as cidades de Cabo Verde, Portugal e Brasil para economizar tempo e recursos
                    List<String> countryCodesToImport = List.of("CV", "PT", "BR");
                    
                    List<Map<String, Object>> citiesData = objectMapper.readValue(
                            new File(getCitiesJsonPath()),
                            new TypeReference<List<Map<String, Object>>>() {}
                    );
                    
                    List<CityEntity> cities = new ArrayList<>();
                    int totalImported = 0;
                    
                    for (Map<String, Object> cityData : citiesData) {
                        String countryCode = (String) cityData.get("country_code");
                        
                        // Importar apenas cidades dos países selecionados
                        if (countryCodesToImport.contains(countryCode)) {
                            String stateCode = (String) cityData.get("state_code");
                            StateEntity state = stateRepository.findByCountryCodeAndStateCode(countryCode, stateCode).orElse(null);
                            
                            if (state != null) {
                                CityEntity city = new CityEntity();
                                city.setName((String) cityData.get("name"));
                                city.setState(state);
                                city.setCountry(state.getCountry()); // Definir o país a partir do estado
                                city.setCountryCode(countryCode);
                                city.setStateCode(stateCode);
                                
                                // Converter latitude e longitude para BigDecimal se necessário
                                String latitude = (String) cityData.get("latitude");
                                String longitude = (String) cityData.get("longitude");
                                city.setLatitude(latitude);
                                city.setLongitude(longitude);
                                
                                // Definir flag como true por padrão
                                city.setFlag(true);
                                
                                cities.add(city);
                                totalImported++;
                                
                                // Importar em lotes de 500 para evitar problemas de memória
                                if (cities.size() >= 500) {
                                    cityRepository.saveAll(cities);
                                    log.info("Importadas {} cidades...", totalImported);
                                    cities.clear();
                                }
                            }
                        }
                    }
                    
                    // Salvar cidades restantes
                    if (!cities.isEmpty()) {
                        cityRepository.saveAll(cities);
                    }
                    
                    log.info("Importação de cidades concluída com sucesso! Total importado: {}", totalImported);
                } catch (IOException e) {
                    log.error("Erro ao importar cidades", e);
                }
            } else {
                log.info("Cidades já existem no banco de dados. Pulando importação.");
            }
        };
    }

    /**
     * Bean para importar localizações de Cabo Verde a partir do arquivo Excel
     */
    @Bean
    @Order(4)
    public CommandLineRunner importCapeVerdeLocations() {
        return args -> {
            log.info("Verificando se é necessário importar localizações de Cabo Verde...");
            if (capeVerdeLocationsRepository.count() == 0) {
                log.info("Iniciando importação de localizações de Cabo Verde a partir do Excel...");
                try (InputStream is = new ClassPathResource(getCvLocationsExcelPath()).getInputStream();
                     Workbook workbook = new XSSFWorkbook(is)) {
                    
                    Sheet sheet = workbook.getSheetAt(0); // Assumindo que os dados estão na primeira planilha
                    
                    List<CapeVerdeLocationsEntity> locations = new ArrayList<>();
                    int totalImported = 0;
                    boolean isHeader = true;
                    
                    for (Row row : sheet) {
                        // Pular a linha de cabeçalho
                        if (isHeader) {
                            isHeader = false;
                            continue;
                        }
                        
                        // Verificar se a linha tem células válidas
                        if (row.getCell(0) == null || row.getCell(0).getStringCellValue().trim().isEmpty()) {
                            continue;
                        }
                        
                        CapeVerdeLocationsEntity location = new CapeVerdeLocationsEntity();
                        
                        // Percorrer as células da linha para encontrar as colunas pelos nomes
                        for (int i = 0; i < row.getLastCellNum(); i++) {
                            Cell headerCell = sheet.getRow(0).getCell(i);
                            String headerName = getCellValueAsString(headerCell).toLowerCase().trim();
                            
                            // Mapear cada coluna de acordo com o nome no cabeçalho
                            if ("ilha".equals(headerName)) {
                                String value = getCellValueAsString(row.getCell(i));
                                location.setIsland(value);
                                location.setName(value); // Usar o mesmo valor para o nome
                            } else if ("concelho".equals(headerName)) {
                                location.setCounty(getCellValueAsString(row.getCell(i)));
                            } else if ("freguesia".equals(headerName)) {
                                location.setParish(getCellValueAsString(row.getCell(i)));
                            } else if ("zona".equals(headerName)) {
                                location.setZone(getCellValueAsString(row.getCell(i)));
                            } else if ("lugar".equals(headerName)) {
                                location.setSubzone(getCellValueAsString(row.getCell(i)));
                            }else if ("nome".equals(headerName)) {
                                location.setName(getCellValueAsString(row.getCell(i)));
                            }
                            // Ignorar outras colunas
                        }
                        
                        // Definir lat e lng como null (ignorar)
                        location.setLat(null);
                        location.setLng(null);
                        
                        locations.add(location);
                        totalImported++;
                        
                        // Importar em lotes de 100 para evitar problemas de memória
                        if (locations.size() >= 100) {
                            capeVerdeLocationsRepository.saveAll(locations);
                            log.info("Importadas {} localizações de Cabo Verde...", totalImported);
                            locations.clear();
                        }
                    }
                    
                    // Salvar localizações restantes
                    if (!locations.isEmpty()) {
                        capeVerdeLocationsRepository.saveAll(locations);
                    }
                    
                    log.info("Importação de localizações de Cabo Verde concluída com sucesso! Total importado: {}", totalImported);
                } catch (IOException e) {
                    log.error("Erro ao importar localizações de Cabo Verde", e);
                }
            } else {
                log.info("Localizações de Cabo Verde já existem no banco de dados. Pulando importação.");
            }
        };
    }
    
    /**
     * Método para importar localizações de Cabo Verde manualmente
     * Este método pode ser chamado a partir de um endpoint REST ou outro ponto de entrada
     */
    public void importCapeVerdeLocationsManually() {
        log.info("Iniciando importação manual de localizações de Cabo Verde a partir do Excel...");
        try (InputStream is = new ClassPathResource(getCvLocationsExcelPath()).getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            
            Sheet sheet = workbook.getSheetAt(0); // Assumindo que os dados estão na primeira planilha
            
            List<CapeVerdeLocationsEntity> locations = new ArrayList<>();
            int totalImported = 0;
            boolean isHeader = true;
            
            for (Row row : sheet) {
                // Pular a linha de cabeçalho
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                
                // Verificar se a linha tem células válidas
                if (row.getCell(0) == null || row.getCell(0).getStringCellValue().trim().isEmpty()) {
                    continue;
                }
                
                CapeVerdeLocationsEntity location = new CapeVerdeLocationsEntity();
                
                // Percorrer as células da linha para encontrar as colunas pelos nomes
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    Cell headerCell = sheet.getRow(0).getCell(i);
                    String headerName = getCellValueAsString(headerCell).toLowerCase().trim();
                    
                    // Mapear cada coluna de acordo com o nome no cabeçalho
                    if ("ilha".equals(headerName)) {
                        String value = getCellValueAsString(row.getCell(i));
                        location.setIsland(value);
                        location.setName(value); // Usar o mesmo valor para o nome
                    } else if ("concelho".equals(headerName)) {
                        location.setCounty(getCellValueAsString(row.getCell(i)));
                    } else if ("freguesia".equals(headerName)) {
                        location.setParish(getCellValueAsString(row.getCell(i)));
                    } else if ("zona".equals(headerName)) {
                        location.setZone(getCellValueAsString(row.getCell(i)));
                    } else if ("lugar".equals(headerName)) {
                        location.setSubzone(getCellValueAsString(row.getCell(i)));
                    }
                    // Ignorar outras colunas
                }
                
                // Definir lat e lng como null (ignorar)
                location.setLat(null);
                location.setLng(null);
                
                // Se o nome não foi definido, usar o valor mais específico disponível
                if (location.getName() == null || location.getName().isEmpty()) {
                    if (location.getSubzone() != null && !location.getSubzone().isEmpty()) {
                        location.setName(location.getSubzone());
                    } else if (location.getZone() != null && !location.getZone().isEmpty()) {
                        location.setName(location.getZone());
                    } else if (location.getParish() != null && !location.getParish().isEmpty()) {
                        location.setName(location.getParish());
                    } else if (location.getCounty() != null && !location.getCounty().isEmpty()) {
                        location.setName(location.getCounty());
                    } else if (location.getIsland() != null && !location.getIsland().isEmpty()) {
                        location.setName(location.getIsland());
                    }
                }
                
                locations.add(location);
                totalImported++;
                
                // Importar em lotes de 100 para evitar problemas de memória
                if (locations.size() >= 100) {
                    capeVerdeLocationsRepository.saveAll(locations);
                    log.info("Importadas {} localizações de Cabo Verde...", totalImported);
                    locations.clear();
                }
            }
            
            // Salvar localizações restantes
            if (!locations.isEmpty()) {
                capeVerdeLocationsRepository.saveAll(locations);
            }
            
            log.info("Importação manual de localizações de Cabo Verde concluída com sucesso! Total importado: {}", totalImported);
        } catch (IOException e) {
            log.error("Erro ao importar localizações de Cabo Verde", e);
            throw new RuntimeException("Erro ao importar localizações de Cabo Verde", e);
        }
    }
    
    /**
     * Método para ler e exibir o cabeçalho do arquivo Excel
     * Isso ajudará a identificar a ordem correta das colunas
     */
    public void readExcelHeader() {
        log.info("Lendo cabeçalho do arquivo Excel de localizações de Cabo Verde...");
        try (InputStream is = new ClassPathResource(getCvLocationsExcelPath()).getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            
            if (headerRow != null) {
                StringBuilder headerInfo = new StringBuilder("Cabeçalho do Excel: ");
                for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                    Cell cell = headerRow.getCell(i);
                    String cellValue = getCellValueAsString(cell);
                    headerInfo.append("Coluna ").append(i).append(": '").append(cellValue).append("', ");
                }
                log.info(headerInfo.toString());
            } else {
                log.info("Não foi possível encontrar a linha de cabeçalho no arquivo Excel.");
            }
            
        } catch (IOException e) {
            log.error("Erro ao ler cabeçalho do arquivo Excel", e);
        }
    }
    
    /**
     * Método auxiliar para obter o valor de uma célula como String
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
