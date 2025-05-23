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
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
@Component
@RequiredArgsConstructor
public class LocationDataImporter {

    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final ObjectMapper objectMapper;
    private final CapeVerdeLocationsRepository capeVerdeLocationsRepository;

    /**
     * Retorna o diretório onde os arquivos JSON estão localizados
     * Tentando primeiro no classpath e depois no sistema de arquivos
     */
    private String getJsonDir() {
        log.info("Buscando diretório de arquivos JSON...");
        
        // Lista de possíveis caminhos relativos para o diretório JSON
        List<String> possiblePaths = Arrays.asList(
            "/json/",
            "/static/json/",
            "/resources/json/",
            "/resources/static/json/"
        );
        
        // Primeiro tenta encontrar no classpath
        for (String path : possiblePaths) {
            try {
                Resource resource = new ClassPathResource(path);
                if (resource.exists()) {
                    log.info("Diretório JSON encontrado no classpath: {}", path);
                    return path;
                }
            } catch (Exception e) {
                log.debug("Não foi possível encontrar o diretório JSON no classpath: {}", path);
            }
        }
        
        // Se não encontrou no classpath, tenta no sistema de arquivos
        String userDir = System.getProperty("user.dir");
        for (String path : possiblePaths) {
            String fullPath = userDir + "/src/main/resources" + path;
            File dir = new File(fullPath);
            if (dir.exists() && dir.isDirectory()) {
                log.info("Diretório JSON encontrado no sistema de arquivos: {}", fullPath);
                return fullPath;
            }
        }
        
        // Se não encontrou em nenhum lugar, usa um padrão
        log.warn("Diretório JSON não encontrado. Usando padrão: src/main/resources/json/");
        return userDir + "/src/main/resources/json/";
    }
    
    /**
     * Obtém o caminho para o arquivo countries.json
     */
    private File getCountriesJsonPath() {
        // Primeiro tenta carregar do classpath
        try {
            Resource resource = new ClassPathResource("json/countries.json");
            if (resource.exists()) {
                log.info("Arquivo countries.json encontrado no classpath");
                return resource.getFile();
            }
        } catch (Exception e) {
            log.debug("Não foi possível carregar countries.json do classpath", e);
        }
        
        // Depois tenta no diretório json
        String jsonDir = getJsonDir();
        String filePath = jsonDir.endsWith("/") ? jsonDir + "countries.json" : jsonDir + "/countries.json";
        File file = new File(filePath);
        
        if (file.exists()) {
            log.info("Arquivo countries.json encontrado em: {}", filePath);
            return file;
        }
        
        // Se não encontrou, verifica em caminhos alternativos específicos
        List<String> specificPaths = Arrays.asList(
            System.getProperty("user.dir") + "/src/main/resources/json/countries.json",
            System.getProperty("user.dir") + "/src/main/resources/static/json/countries.json"
        );
        
        for (String path : specificPaths) {
            File specificFile = new File(path);
            if (specificFile.exists()) {
                log.info("Arquivo countries.json encontrado em caminho específico: {}", path);
                return specificFile;
            }
        }
        
        log.warn("Arquivo countries.json não encontrado em nenhum local");
        return file; // Retorna o último arquivo tentado, mesmo que não exista
    }
    
    /**
     * Obtém o caminho para o arquivo states.json
     */
    private File getStatesJsonPath() {
        // Primeiro tenta carregar do classpath
        try {
            Resource resource = new ClassPathResource("json/states.json");
            if (resource.exists()) {
                log.info("Arquivo states.json encontrado no classpath");
                return resource.getFile();
            }
        } catch (Exception e) {
            log.debug("Não foi possível carregar states.json do classpath", e);
        }
        
        // Depois tenta no diretório json
        String jsonDir = getJsonDir();
        String filePath = jsonDir.endsWith("/") ? jsonDir + "states.json" : jsonDir + "/states.json";
        File file = new File(filePath);
        
        if (file.exists()) {
            log.info("Arquivo states.json encontrado em: {}", filePath);
            return file;
        }
        
        // Se não encontrou, verifica em caminhos alternativos específicos
        List<String> specificPaths = Arrays.asList(
            System.getProperty("user.dir") + "/src/main/resources/json/states.json",
            System.getProperty("user.dir") + "/src/main/resources/static/json/states.json"
        );
        
        for (String path : specificPaths) {
            File specificFile = new File(path);
            if (specificFile.exists()) {
                log.info("Arquivo states.json encontrado em caminho específico: {}", path);
                return specificFile;
            }
        }
        
        log.warn("Arquivo states.json não encontrado em nenhum local");
        return file; // Retorna o último arquivo tentado, mesmo que não exista
    }
    
    /**
     * Obtém o caminho para o arquivo cities.json
     */
    private File getCitiesJsonPath() {
        // Primeiro tenta carregar do classpath
        try {
            Resource resource = new ClassPathResource("json/cities.json");
            if (resource.exists()) {
                log.info("Arquivo cities.json encontrado no classpath");
                return resource.getFile();
            }
        } catch (Exception e) {
            log.debug("Não foi possível carregar cities.json do classpath", e);
        }
        
        // Depois tenta no diretório json
        String jsonDir = getJsonDir();
        String filePath = jsonDir.endsWith("/") ? jsonDir + "cities.json" : jsonDir + "/cities.json";
        File file = new File(filePath);
        
        if (file.exists()) {
            log.info("Arquivo cities.json encontrado em: {}", filePath);
            return file;
        }
        
        // Se não encontrou, verifica em caminhos alternativos específicos
        List<String> specificPaths = Arrays.asList(
            System.getProperty("user.dir") + "/src/main/resources/json/cities.json",
            System.getProperty("user.dir") + "/src/main/resources/static/json/cities.json"
        );
        
        for (String path : specificPaths) {
            File specificFile = new File(path);
            if (specificFile.exists()) {
                log.info("Arquivo cities.json encontrado em caminho específico: {}", path);
                return specificFile;
            }
        }
        
        log.warn("Arquivo cities.json não encontrado em nenhum local");
        return file; // Retorna o último arquivo tentado, mesmo que não exista
    }

    /**
     * Bean para importar países
     */
    @Bean
    @Order(1)
    @Transactional
    public CommandLineRunner importCountries() {
        return args -> {
            try {
                // Verificar se já existem países no banco de dados
                long count = countryRepository.count();
                log.info("Contagem atual de países no banco: {}", count);
                
                log.info("Iniciando importação de países...");
                
                File jsonFile = getCountriesJsonPath();
                
                if (!jsonFile.exists()) {
                    log.error("Arquivo countries.json não encontrado em nenhum caminho. Pulando importação.");
                    return;
                }
                
                // Usar FileInputStream diretamente
                try (InputStream is = new FileInputStream(jsonFile)) {
                    log.info("Arquivo countries.json encontrado. Iniciando importação...");
                    
                    List<Map<String, Object>> countriesData = objectMapper.readValue(
                        is, new TypeReference<List<Map<String, Object>>>() {});
                    
                    log.info("Processando {} países do arquivo JSON...", countriesData.size());
                    
                    List<CountryEntity> countriesToSave = new ArrayList<>();
                    
                    for (Map<String, Object> countryData : countriesData) {
                        try {
                            CountryEntity country = new CountryEntity();
                            country.setId(Long.valueOf(countryData.get("id").toString()));
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
                            country.setNativeName((String) countryData.get("native_name"));
                            country.setRegion((String) countryData.get("region"));
                            country.setSubregion((String) countryData.get("subregion"));
                            
                            // Latitude e longitude são do tipo String no entity
                            country.setLatitude(countryData.get("latitude") != null ? countryData.get("latitude").toString() : null);
                            country.setLongitude(countryData.get("longitude") != null ? countryData.get("longitude").toString() : null);
                            
                            countriesToSave.add(country);
                            
                            // Salvar em lotes de 100 para evitar problemas de memória
                            if (countriesToSave.size() >= 100) {
                                countryRepository.saveAll(countriesToSave);
                                countriesToSave.clear();
                                log.info("Lote de 100 países importado.");
                            }
                        } catch (Exception e) {
                            log.warn("Erro ao processar país: {}", countryData, e);
                        }
                    }
                    
                    // Salvar países restantes
                    if (!countriesToSave.isEmpty()) {
                        countryRepository.saveAll(countriesToSave);
                        log.info("Lote final de {} países importado.", countriesToSave.size());
                    }
                    
                    log.info("Importação de países concluída com sucesso.");
                }
            } catch (Exception e) {
                // Capturar qualquer exceção, incluindo TableNotFoundException, para permitir que a aplicação continue
                log.error("Erro ao importar países: {}", e.getMessage(), e);
            }
        };
    }
    
    /**
     * Bean para importar estados
     */
    @Bean
    @Order(2)
    @Transactional
    public CommandLineRunner importStates() {
        return args -> {
            try {
                // Verificar se já existem estados no banco de dados
                long count = stateRepository.count();
                log.info("Contagem atual de estados no banco: {}", count);
                
                log.info("Iniciando importação de estados...");
                
                File jsonFile = getStatesJsonPath();
                
                if (!jsonFile.exists()) {
                    log.error("Arquivo states.json não encontrado em nenhum caminho. Pulando importação.");
                    return;
                }
                
                // Usar FileInputStream diretamente
                try (InputStream is = new FileInputStream(jsonFile)) {
                    log.info("Arquivo states.json encontrado. Iniciando importação...");
                    
                    List<Map<String, Object>> statesData = objectMapper.readValue(
                        is, new TypeReference<List<Map<String, Object>>>() {});
                    
                    log.info("Processando {} estados do arquivo JSON...", statesData.size());
                    
                    List<StateEntity> statesToSave = new ArrayList<>();
                    Map<Long, CountryEntity> countryCache = new HashMap<>();
                    
                    // Importar apenas estados de países específicos (BR, PT, CV)
                    List<String> countryCodesToImport = List.of("CV", "PT", "BR");
                    
                    for (Map<String, Object> stateData : statesData) {
                        try {
                            String countryCode = (String) stateData.get("country_code");
                            
                            // Ignorar estados de países que não estão na lista
                            if (!countryCodesToImport.contains(countryCode)) {
                                continue;
                            }
                            
                            // Obter país pelo ID
                            Long countryId = Long.valueOf(stateData.get("country_id").toString());
                            CountryEntity country = countryCache.get(countryId);
                            if (country == null) {
                                CountryEntity countryEntity = countryRepository.findById(countryId).orElse(null);
                                if (countryEntity == null) {
                                    log.warn("País com ID {} não encontrado, pulando estado: {}", countryId, stateData.get("name"));
                                    continue;
                                }
                                country = countryEntity;
                                countryCache.put(countryId, country);
                            }
                            
                            // Criar entidade de estado
                            StateEntity state = new StateEntity();
                            state.setId(Long.valueOf(stateData.get("id").toString()));
                            state.setName((String) stateData.get("name"));
                            state.setCountry(country);
                            state.setCountryCode(countryCode);
                            state.setFipsCode((String) stateData.get("fips_code"));
                            state.setIso2((String) stateData.get("iso2"));
                            state.setType((String) stateData.get("type"));
                            
                            // Latitude e longitude são decimais no entity
                            state.setLatitude(stateData.get("latitude") != null
                                ? stateData.get("latitude").toString() : null);
                            state.setLongitude(stateData.get("longitude") != null
                                ? stateData.get("longitude").toString(): null);
                            
                            state.setCreatedAt(null);
                            state.setUpdatedAt(null);
                            state.setWikiDataId((String) stateData.get("wikiDataId"));
                            
                            statesToSave.add(state);
                            
                            // Salvar em lotes de 100 para evitar problemas de memória
                            if (statesToSave.size() >= 100) {
                                stateRepository.saveAll(statesToSave);
                                statesToSave.clear();
                                log.info("Lote de 100 estados importado.");
                            }
                        } catch (Exception e) {
                            log.warn("Erro ao processar estado: {}", stateData, e);
                        }
                    }
                    
                    // Salvar estados restantes
                    if (!statesToSave.isEmpty()) {
                        stateRepository.saveAll(statesToSave);
                        log.info("Lote final de {} estados importado.", statesToSave.size());
                    }
                    
                    log.info("Importação de estados concluída com sucesso.");
                }
            } catch (Exception e) {
                // Capturar qualquer exceção, incluindo TableNotFoundException, para permitir que a aplicação continue
                log.error("Erro ao importar estados: {}", e.getMessage(), e);
            }
        };
    }
    
    /**
     * Bean para importar cidades
     */
    @Bean
    @Order(3)
    @Transactional
    public CommandLineRunner importCities() {
        return args -> {
            try {
                // Verificar se já existem cidades no banco de dados
                long count = cityRepository.count();
                log.info("Contagem atual de cidades no banco: {}", count);
                
                log.info("Iniciando importação de cidades...");
                
                // Importar apenas cidades de países específicos (BR, PT, CV)
                List<String> countryCodesToImport = List.of("CV", "PT", "BR");
                
                File jsonFile = getCitiesJsonPath();
                
                if (!jsonFile.exists()) {
                    log.error("Arquivo cities.json não encontrado em nenhum caminho. Pulando importação.");
                    return;
                }
                
                // Usar FileInputStream diretamente
                try (InputStream is = new FileInputStream(jsonFile)) {
                    log.info("Arquivo cities.json encontrado. Iniciando importação...");
                    
                    List<Map<String, Object>> citiesData = objectMapper.readValue(
                        is, new TypeReference<List<Map<String, Object>>>() {});
                    
                    log.info("Processando {} cidades do arquivo JSON...", citiesData.size());
                    
                    List<CityEntity> citiesToSave = new ArrayList<>();
                    Map<Long, CountryEntity> countryCache = new HashMap<>();
                    Map<Long, StateEntity> stateCache = new HashMap<>();
                    
                    int processedCount = 0;
                    int importedCount = 0;
                    
                    for (Map<String, Object> cityData : citiesData) {
                        try {
                            processedCount++;
                            
                            // Log de progresso a cada 1000 registros
                            if (processedCount % 1000 == 0) {
                                log.info("Processados {} registros de cidades...", processedCount);
                            }
                            
                            String countryCode = (String) cityData.get("country_code");
                            
                            // Ignorar cidades de países que não estão na lista
                            if (!countryCodesToImport.contains(countryCode)) {
                                continue;
                            }
                            
                            // Obter país pelo ID
                            Long countryId = Long.valueOf(cityData.get("country_id").toString());
                            CountryEntity country = countryCache.get(countryId);
                            if (country == null) {
                                CountryEntity countryEntity = countryRepository.findById(countryId).orElse(null);
                                if (countryEntity == null) {
                                    log.warn("País com ID {} não encontrado, pulando cidade: {}", countryId, cityData.get("name"));
                                    continue;
                                }
                                country = countryEntity;
                                countryCache.put(countryId, country);
                            }
                            
                            // Obter estado pelo ID
                            Long stateId = Long.valueOf(cityData.get("state_id").toString());
                            StateEntity state = stateCache.get(stateId);
                            if (state == null) {
                                StateEntity stateEntity = stateRepository.findById(stateId).orElse(null);
                                if (stateEntity == null) {
                                    log.warn("Estado com ID {} não encontrado, pulando cidade: {}", stateId, cityData.get("name"));
                                    continue;
                                }
                                state = stateEntity;
                                stateCache.put(stateId, state);
                            }
                            
                            // Criar entidade de cidade
                            CityEntity city = new CityEntity();
                            city.setId(Long.valueOf(cityData.get("id").toString()));
                            city.setName((String) cityData.get("name"));
                            city.setState(state);
                            city.setStateCode((String) cityData.get("state_code"));
                            city.setCountry(country);
                            city.setCountryCode(countryCode);
                            
                            // Latitude e longitude são decimais no entity
                            city.setLatitude(cityData.get("latitude") != null 
                                ? cityData.get("latitude").toString() : null);
                            city.setLongitude(cityData.get("longitude") != null 
                                ? cityData.get("longitude").toString() : null);
                            
                            city.setCreatedAt(null);
                            city.setUpdatedAt(null);
                            city.setWikiDataId((String) cityData.get("wikiDataId"));
                            
                            citiesToSave.add(city);
                            importedCount++;
                            
                            // Salvar em lotes de 100 para evitar problemas de memória
                            if (citiesToSave.size() >= 100) {
                                cityRepository.saveAll(citiesToSave);
                                citiesToSave.clear();
                                log.info("Lote de 100 cidades importado. Total importado: {}", importedCount);
                            }
                        } catch (Exception e) {
                            log.warn("Erro ao processar cidade: {}", cityData, e);
                        }
                    }
                    
                    // Salvar cidades restantes
                    if (!citiesToSave.isEmpty()) {
                        cityRepository.saveAll(citiesToSave);
                        log.info("Lote final de {} cidades importado. Total importado: {}", citiesToSave.size(), importedCount);
                    }
                    
                    log.info("Importação de cidades concluída com sucesso. Total de cidades importadas: {}", importedCount);
                }
            } catch (Exception e) {
                // Capturar qualquer exceção para permitir que a aplicação continue
                log.error("Erro ao importar cidades: {}", e.getMessage(), e);
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
        try (Workbook workbook = WorkbookFactory.create(is)) {
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
            
            try (Workbook workbook = WorkbookFactory.create(is)) {
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
            
            try (Workbook workbook = WorkbookFactory.create(is)) {
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
    
    /**
     * Obtém um InputStream para o arquivo especificado, procurando em várias localizações possíveis.
     * 
     * @param filename Nome do arquivo a ser carregado
     * @return InputStream do arquivo, ou null se não encontrado
     */
    private InputStream getResourceAsStream(String filename) {
        log.info("Tentando carregar o arquivo: {}", filename);
        
        // Primeiro tenta como resource no classpath
        try {
            ClassPathResource resource = new ClassPathResource(filename);
            if (resource.exists()) {
                log.info("Arquivo encontrado como ClassPathResource: {}", filename);
                return resource.getInputStream();
            }
        } catch (Exception e) {
            log.warn("Não foi possível carregar o arquivo como ClassPathResource: {}", e.getMessage());
        }
        
        // Tenta no diretório de recursos
        try {
            Resource resource = new ClassPathResource("json/" + filename);
            if (resource.exists()) {
                log.info("Arquivo encontrado como ClassPathResource: json/{}", filename);
                return resource.getInputStream();
            }
        } catch (Exception e) {
            log.warn("Não foi possível carregar o arquivo como ClassPathResource em /json: {}", e.getMessage());
        }
        
        // Tenta no sistema de arquivos
        String resourcesPath = System.getProperty("user.dir") + "/src/main/resources/";
        List<String> possiblePaths = Arrays.asList(
            resourcesPath + filename,
            resourcesPath + "json/" + filename,
            resourcesPath + "static/json/" + filename,
            resourcesPath + "static/" + filename
        );
        
        for (String path : possiblePaths) {
            try {
                File file = new File(path);
                if (file.exists()) {
                    log.info("Arquivo encontrado no sistema de arquivos: {}", path);
                    return new FileInputStream(file);
                }
            } catch (Exception e) {
                log.warn("Não foi possível carregar o arquivo do caminho: {}: {}", path, e.getMessage());
            }
        }
        
        log.error("Arquivo {} não encontrado em nenhum local!", filename);
        return null;
    }
}
