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
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    /**
     * Verifica se um recurso existe no classpath
     */
    private boolean resourceExists(String path) {
        try {
            Resource resource = new ClassPathResource(path);
            return resource.exists();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Tenta carregar um recurso do classpath ou do sistema de arquivos,
     * verificando várias localizações possíveis
     */
    private InputStream getResourceAsStream(String filename) {
        List<String> classpathLocations = Arrays.asList(
            filename,                    // Diretamente na raiz do classpath
            "json/" + filename,          // Na pasta json do classpath
            "app/" + filename,           // Na pasta app do classpath
            "static/" + filename,        // Na pasta static do classpath
            "resources/" + filename      // Na pasta resources do classpath
        );
        
        List<String> fileSystemLocations = Arrays.asList(
            System.getProperty("user.dir") + "/" + filename,                 // Na raiz do projeto
            System.getProperty("user.dir") + "/json/" + filename,            // Na pasta json do projeto
            System.getProperty("user.dir") + "/app/" + filename,             // Na pasta app do projeto
            System.getProperty("user.home") + "/" + filename,                // Na pasta home do usuário
            "/app/" + filename,                                              // Na pasta app (Docker)
            "/app/json/" + filename,                                         // Na pasta app/json (Docker)
            "/app/resources/" + filename                                     // Na pasta app/resources (Docker)
        );
        
        // Primeiro, tenta todas as localizações do classpath
        for (String location : classpathLocations) {
            try {
                if (resourceExists(location)) {
                    log.info("Recurso encontrado no classpath: {}", location);
                    return new ClassPathResource(location).getInputStream();
                }
            } catch (Exception e) {
                // Ignora e tenta a próxima localização
            }
        }
        
        // Em seguida, tenta todas as localizações do sistema de arquivos
        for (String location : fileSystemLocations) {
            try {
                File file = new File(location);
                if (file.exists()) {
                    log.info("Recurso encontrado no sistema de arquivos: {}", location);
                    return new FileInputStream(file);
                }
            } catch (Exception e) {
                // Ignora e tenta a próxima localização
            }
        }
        
        log.warn("Recurso '{}' não encontrado em nenhuma localização.", filename);
        return null;
    }

    /**
     * Bean para importar países
     */
    @Bean
    @Order(1)
    public CommandLineRunner importCountries() {
        return args -> {
            // Verificar se já existem países no banco de dados
            if (countryRepository.count() > 0) {
                log.info("Países já existem no banco de dados. Pulando importação.");
                return;
            }
            
            log.info("Iniciando importação de países...");
            
            try (InputStream is = getResourceAsStream("countries.json")) {
                if (is == null) {
                    log.warn("Arquivo countries.json não encontrado. Pulando importação de países.");
                    return;
                }
                
                List<Map<String, Object>> countriesData = objectMapper.readValue(
                        is,
                        new TypeReference<List<Map<String, Object>>>() {}
                );
                
                List<CountryEntity> countriesToSave = new ArrayList<>();
                
                for (Map<String, Object> countryData : countriesData) {
                    CountryEntity country = new CountryEntity();
                    country.setId(Long.valueOf(countryData.get("id").toString()));
                    country.setName((String) countryData.get("name"));
                    country.setIso3((String) countryData.get("iso3"));
                    country.setIso2((String) countryData.get("iso2"));
                    country.setNumericCode((String) countryData.get("numeric_code"));
                    country.setPhonecode((String) countryData.get("phone_code"));
                    country.setCapital((String) countryData.get("capital"));
                    country.setCurrency((String) countryData.get("currency"));
                    country.setCurrencyName((String) countryData.get("currency_name"));
                    country.setCurrencySymbol((String) countryData.get("currency_symbol"));
                    country.setTld((String) countryData.get("tld"));
                    country.setNativeName((String) countryData.get("native"));
                    country.setRegion((String) countryData.get("region"));
                    country.setSubregion((String) countryData.get("subregion"));
                    
                    // Latitude e longitude são do tipo String no entity
                    if (countryData.get("latitude") != null) {
                        country.setLatitude(countryData.get("latitude").toString());
                    }
                    if (countryData.get("longitude") != null) {
                        country.setLongitude(countryData.get("longitude").toString());
                    }
                    
                    country.setEmoji((String) countryData.get("emoji"));
                    country.setEmojiU((String) countryData.get("emojiU"));
                    
                    countriesToSave.add(country);
                }
                
                countryRepository.saveAll(countriesToSave);
                log.info("Importação de {} países concluída com sucesso.", countriesToSave.size());
            } catch (Exception e) {
                log.error("Erro ao importar países", e);
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
            // Verificar se já existem estados no banco de dados
            if (stateRepository.count() > 0) {
                log.info("Estados já existem no banco de dados. Pulando importação.");
                return;
            }
            
            log.info("Iniciando importação de estados...");
            
            try (InputStream is = getResourceAsStream("states.json")) {
                if (is == null) {
                    log.warn("Arquivo states.json não encontrado. Pulando importação de estados.");
                    return;
                }
                
                List<Map<String, Object>> statesData = objectMapper.readValue(
                        is,
                        new TypeReference<List<Map<String, Object>>>() {}
                );
                
                List<StateEntity> statesToSave = new ArrayList<>();
                Map<Long, CountryEntity> countryCache = new HashMap<>();
                
                // Importar apenas estados de países específicos (BR, PT, CV)
                List<String> countryCodesToImport = List.of("CV", "PT", "BR");
                
                for (Map<String, Object> stateData : statesData) {
                    Map<String, Object> countryData = (Map<String, Object>) stateData.get("country");
                    String countryCode = (String) countryData.get("iso2");
                    
                    if (countryCodesToImport.contains(countryCode)) {
                        StateEntity state = new StateEntity();
                        state.setId(Long.valueOf(stateData.get("id").toString()));
                        state.setName((String) stateData.get("name"));
                        
                        // Procurar o país no cache ou no banco
                        Long countryId = Long.valueOf(countryData.get("id").toString());
                        CountryEntity country = countryCache.get(countryId);
                        if (country == null) {
                            Optional<CountryEntity> countryOpt = countryRepository.findById(countryId);
                            if (countryOpt.isPresent()) {
                                country = countryOpt.get();
                                countryCache.put(countryId, country);
                            } else {
                                log.warn("País com ID {} não encontrado. Pulando estado {}.", countryId, state.getName());
                                continue;
                            }
                        }
                        
                        state.setCountry(country);
                        state.setCountryCode((String) countryData.get("iso2"));
                        state.setStateCode((String) stateData.get("state_code"));
                        state.setType((String) stateData.get("type"));
                        
                        // Latitude e longitude são do tipo String no entity
                        if (stateData.get("latitude") != null) {
                            state.setLatitude(stateData.get("latitude").toString());
                        }
                        if (stateData.get("longitude") != null) {
                            state.setLongitude(stateData.get("longitude").toString());
                        }
                        
                        statesToSave.add(state);
                    }
                }
                
                stateRepository.saveAll(statesToSave);
                log.info("Importação de {} estados concluída com sucesso.", statesToSave.size());
            } catch (Exception e) {
                log.error("Erro ao importar estados", e);
            }
        };
    }
    
    /**
     * Bean para importar cidades
     */
    @Bean
    @Order(3)
    public CommandLineRunner importCities() {
        return args -> {
            // Verificar se já existem cidades no banco de dados
            if (cityRepository.count() > 0) {
                log.info("Cidades já existem no banco de dados. Pulando importação.");
                return;
            }
            
            log.info("Iniciando importação de cidades...");
            
            // Importar apenas cidades de países específicos (BR, PT, CV)
            List<String> countryCodesToImport = List.of("CV", "PT", "BR");
            
            try (InputStream is = getResourceAsStream("cities.json")) {
                if (is == null) {
                    log.warn("Arquivo cities.json não encontrado. Pulando importação de cidades.");
                    return;
                }
                
                List<Map<String, Object>> citiesData = objectMapper.readValue(
                        is,
                        new TypeReference<List<Map<String, Object>>>() {}
                );
                
                List<CityEntity> citiesToSave = new ArrayList<>();
                Map<Long, CountryEntity> countryCache = new HashMap<>();
                Map<Long, StateEntity> stateCache = new HashMap<>();
                
                for (Map<String, Object> cityData : citiesData) {
                    Map<String, Object> countryData = (Map<String, Object>) cityData.get("country");
                    String countryCode = (String) countryData.get("iso2");
                    
                    if (countryCodesToImport.contains(countryCode)) {
                        CityEntity city = new CityEntity();
                        city.setId(Long.valueOf(cityData.get("id").toString()));
                        city.setName((String) cityData.get("name"));
                        
                        // Obter dados do estado
                        Map<String, Object> stateData = (Map<String, Object>) cityData.get("state");
                        Long stateId = Long.valueOf(stateData.get("id").toString());
                        StateEntity state = stateCache.get(stateId);
                        if (state == null) {
                            Optional<StateEntity> stateOpt = stateRepository.findById(stateId);
                            if (stateOpt.isPresent()) {
                                state = stateOpt.get();
                                stateCache.put(stateId, state);
                            } else {
                                log.warn("Estado com ID {} não encontrado. Pulando cidade {}.", stateId, city.getName());
                                continue;
                            }
                        }
                        city.setState(state);
                        city.setStateCode((String) stateData.get("state_code"));
                        
                        // Obter dados do país
                        Long countryId = Long.valueOf(countryData.get("id").toString());
                        CountryEntity country = countryCache.get(countryId);
                        if (country == null) {
                            Optional<CountryEntity> countryOpt = countryRepository.findById(countryId);
                            if (countryOpt.isPresent()) {
                                country = countryOpt.get();
                                countryCache.put(countryId, country);
                            } else {
                                log.warn("País com ID {} não encontrado. Pulando cidade {}.", countryId, city.getName());
                                continue;
                            }
                        }
                        city.setCountry(country);
                        city.setCountryCode(countryCode);
                        
                        // Latitude e longitude são do tipo String no entity
                        if (cityData.get("latitude") != null) {
                            city.setLatitude(cityData.get("latitude").toString());
                        }
                        if (cityData.get("longitude") != null) {
                            city.setLongitude(cityData.get("longitude").toString());
                        }
                        
                        citiesToSave.add(city);
                    }
                }
                
                cityRepository.saveAll(citiesToSave);
                log.info("Importação de {} cidades concluída com sucesso.", citiesToSave.size());
            } catch (Exception e) {
                log.error("Erro ao importar cidades", e);
            }
        };
    }

    /**
     * Bean para importar localizações de Cabo Verde
     */
    @Bean
    @Order(4)
    public CommandLineRunner importCapeVerdeLocations() {
        return args -> {
            log.info("Verificando se é necessário importar localizações de Cabo Verde...");
            if (capeVerdeLocationsRepository.count() == 0) {
                log.info("Iniciando importação de localizações de Cabo Verde a partir do arquivo...");
                
                // Tenta primeiro carregar o arquivo Excel
                try (InputStream is = getResourceAsStream("codigo-paises-lugares-cv.xlsx")) {
                    if (is != null) {
                        importFromExcel(is);
                    } else {
                        // Se não encontrar o Excel, tenta CSV
                        log.info("Arquivo Excel não encontrado. Tentando importar do CSV...");
                        try (InputStream csvIs = getResourceAsStream("codigo-paises-lugares-cv.csv")) {
                            if (csvIs != null) {
                                importFromCsv(csvIs);
                            } else {
                                log.warn("Nenhum arquivo de localizações de Cabo Verde (Excel ou CSV) foi encontrado. Pulando importação.");
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("Erro ao importar localizações de Cabo Verde", e);
                }
            } else {
                log.info("Localizações de Cabo Verde já existem no banco de dados. Pulando importação.");
            }
        };
    }
    
    /**
     * Importa localizações a partir de um arquivo Excel
     */
    private void importFromExcel(InputStream is) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0); // Assume primeira planilha
            
            List<CapeVerdeLocationsEntity> locationsToSave = new ArrayList<>();
            
            // Pular linha de cabeçalho
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                CapeVerdeLocationsEntity location = new CapeVerdeLocationsEntity();
                
                // Extrair valores das células
                try {
                    location.setId(getCellValueAsLong(row.getCell(0)));
                    location.setIsland(getCellValueAsString(row.getCell(1)));
                    location.setCounty(getCellValueAsString(row.getCell(2)));
                    location.setParish(getCellValueAsString(row.getCell(3)));
                    location.setZone(getCellValueAsString(row.getCell(4)));
                    location.setSubzone(getCellValueAsString(row.getCell(5)));
                    location.setName(getCellValueAsString(row.getCell(6)));
                    
                    locationsToSave.add(location);
                } catch (Exception e) {
                    log.warn("Erro ao processar linha {} do Excel", i, e);
                }
            }
            
            if (!locationsToSave.isEmpty()) {
                capeVerdeLocationsRepository.saveAll(locationsToSave);
                log.info("Importação de {} localizações de Cabo Verde concluída com sucesso.", locationsToSave.size());
            } else {
                log.warn("Nenhuma localização de Cabo Verde foi extraída do arquivo Excel.");
            }
        }
    }
    
    /**
     * Importa localizações a partir de um arquivo CSV
     */
    private void importFromCsv(InputStream is) throws IOException {
        List<CapeVerdeLocationsEntity> locationsToSave = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        
        // Ler e descartar o cabeçalho
        String header = reader.readLine();
        if (header == null) {
            log.warn("Arquivo CSV vazio ou inválido.");
            return;
        }
        
        String line;
        int lineNumber = 1;
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            String[] values = line.split(",");
            
            if (values.length < 7) {
                log.warn("Linha {} do CSV tem menos colunas que o esperado", lineNumber);
                continue;
            }
            
            try {
                CapeVerdeLocationsEntity location = new CapeVerdeLocationsEntity();
                location.setId(Long.parseLong(values[0]));
                location.setIsland(values[1]);
                location.setCounty(values[2]);
                location.setParish(values[3]);
                location.setZone(values[4]);
                location.setSubzone(values[5]);
                location.setName(values[6]);
                
                locationsToSave.add(location);
            } catch (Exception e) {
                log.warn("Erro ao processar linha {} do CSV: {}", lineNumber, e.getMessage());
            }
        }
        
        if (!locationsToSave.isEmpty()) {
            capeVerdeLocationsRepository.saveAll(locationsToSave);
            log.info("Importação de {} localizações de Cabo Verde concluída com sucesso a partir do CSV.", locationsToSave.size());
        } else {
            log.warn("Nenhuma localização de Cabo Verde foi extraída do arquivo CSV.");
        }
    }
    
    /**
     * Método para importar localizações de Cabo Verde manualmente
     * Este método pode ser chamado a partir de um endpoint REST ou outro ponto de entrada
     */
    public void importCapeVerdeLocationsManually() {
        log.info("Iniciando importação manual de localizações de Cabo Verde a partir do Excel...");
        
        try (InputStream is = getResourceAsStream("codigo-paises-lugares-cv.xlsx")) {
            if (is == null) {
                log.warn("Arquivo codigo-paises-lugares-cv.xlsx não encontrado. Pulando importação de localizações de Cabo Verde.");
                return;
            }
            
            try (Workbook workbook = new XSSFWorkbook(is)) {
                Sheet sheet = workbook.getSheetAt(0); // Assume primeira planilha
                
                List<CapeVerdeLocationsEntity> locationsToSave = new ArrayList<>();
                
                // Pular linha de cabeçalho
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;
                    
                    CapeVerdeLocationsEntity location = new CapeVerdeLocationsEntity();
                    
                    // Extrair valores das células
                    try {
                        location.setId(getCellValueAsLong(row.getCell(0)));
                        location.setIsland(getCellValueAsString(row.getCell(1)));
                        location.setCounty(getCellValueAsString(row.getCell(2)));
                        location.setParish(getCellValueAsString(row.getCell(3)));
                        location.setZone(getCellValueAsString(row.getCell(4)));
                        location.setSubzone(getCellValueAsString(row.getCell(5)));
                        location.setName(getCellValueAsString(row.getCell(6)));
                        
                        locationsToSave.add(location);
                    } catch (Exception e) {
                        log.warn("Erro ao processar linha {} do Excel", i, e);
                    }
                }
                
                if (!locationsToSave.isEmpty()) {
                    capeVerdeLocationsRepository.saveAll(locationsToSave);
                    log.info("Importação manual de {} localizações de Cabo Verde concluída com sucesso.", locationsToSave.size());
                } else {
                    log.warn("Nenhuma localização de Cabo Verde foi extraída do arquivo Excel.");
                }
            }
        } catch (Exception e) {
            log.error("Erro ao importar localizações de Cabo Verde manualmente", e);
        }
    }
    
    /**
     * Método para ler e exibir o cabeçalho do arquivo Excel
     * Isso ajudará a identificar a ordem correta das colunas
     */
    public void readExcelHeader() {
        try (InputStream is = getResourceAsStream("codigo-paises-lugares-cv.xlsx")) {
            if (is == null) {
                log.warn("Arquivo codigo-paises-lugares-cv.xlsx não encontrado.");
                return;
            }
            
            try (Workbook workbook = new XSSFWorkbook(is)) {
                Sheet sheet = workbook.getSheetAt(0); // Assume primeira planilha
                Row headerRow = sheet.getRow(0);
                
                if (headerRow != null) {
                    for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                        Cell cell = headerRow.getCell(i);
                        log.info("Coluna {}: {}", i, getCellValueAsString(cell));
                    }
                }
            }
        } catch (Exception e) {
            log.error("Erro ao ler cabeçalho do Excel", e);
        }
    }
    
    /**
     * Método auxiliar para obter o valor de uma célula como String
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (Exception ex) {
                        return null;
                    }
                }
            default:
                return null;
        }
    }
    
    /**
     * Método auxiliar para obter o valor de uma célula como Long
     */
    private Long getCellValueAsLong(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case NUMERIC:
                return (long) cell.getNumericCellValue();
            case STRING:
                try {
                    return Long.parseLong(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }
}
