package cv.ntinnovation.vintipay.config;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.CityEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.CountryEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.RegionEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.StateEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.SubRegionEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.CapeVerdeLocationsEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.CityRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.CountryRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.RegionRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.StateRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.SubRegionRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.CapeVerdeLocationsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CsvDataImportService {

    private static final Logger logger = LoggerFactory.getLogger(CsvDataImportService.class);

    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final RegionRepository regionRepository;
    private final SubRegionRepository subRegionRepository;
    private final CapeVerdeLocationsRepository capeVerdeLocationsRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public CsvDataImportService(CountryRepository countryRepository,
                             StateRepository stateRepository,
                             CityRepository cityRepository,
                             RegionRepository regionRepository,
                             SubRegionRepository subRegionRepository,
                             CapeVerdeLocationsRepository capeVerdeLocationsRepository) {
        this.countryRepository = countryRepository;
        this.stateRepository = stateRepository;
        this.cityRepository = cityRepository;
        this.regionRepository = regionRepository;
        this.subRegionRepository = subRegionRepository;
        this.capeVerdeLocationsRepository = capeVerdeLocationsRepository;
    }

    /**
     * Importa regiões a partir do arquivo CSV
     */
    @Transactional
    public int importRegions() throws IOException, CsvValidationException {
        String csvContent = getResourceAsString("csv/regions.csv");
        int importCount = 0;

        try (CSVReader csvReader = new CSVReader(new StringReader(csvContent))) {
            // Ler o cabeçalho
            String[] headers = csvReader.readNext();

            // Mapear os índices dos cabeçalhos
            Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerMap.put(headers[i], i);
            }

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                // Verificar se a região já existe
                String idStr = getValueOrEmpty(line, headerMap, "id");
                if (idStr.isEmpty()) {
                    logger.warn("Linha ignorada: ID da região não encontrado");
                    continue;
                }
                
                Long id = Long.parseLong(idStr);
                Optional<RegionEntity> existingRegion = regionRepository.findById(id);

                if (existingRegion.isPresent()) {
                    // Atualizar região existente
                    RegionEntity region = existingRegion.get();
                    region.setName(getValueOrEmpty(line, headerMap, "name"));
                    region.setWikiDataId(getValueOrEmpty(line, headerMap, "wikiDataId"));
                    regionRepository.save(region);
                } else {
                    // Criar nova região
                    RegionEntity region = new RegionEntity();
                    region.setId(id);
                    region.setName(getValueOrEmpty(line, headerMap, "name"));
                    region.setWikiDataId(getValueOrEmpty(line, headerMap, "wikiDataId"));
                    regionRepository.save(region);
                    importCount++;
                }
            }
        }

        return importCount;
    }

    /**
     * Importa sub-regiões a partir do arquivo CSV
     */
    @Transactional
    public int importSubRegions() throws IOException, CsvValidationException {
        String csvContent = getResourceAsString("csv/subregions.csv");
        int importCount = 0;

        try (CSVReader csvReader = new CSVReader(new StringReader(csvContent))) {
            // Ler o cabeçalho
            String[] headers = csvReader.readNext();

            // Mapear os índices dos cabeçalhos
            Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerMap.put(headers[i], i);
            }

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                // Verificar se a sub-região já existe
                String idStr = getValueOrEmpty(line, headerMap, "id");
                if (idStr.isEmpty()) {
                    logger.warn("Linha ignorada: ID da sub-região não encontrado");
                    continue;
                }
                
                // Verificar se a região pai existe
                String regionIdStr = getValueOrEmpty(line, headerMap, "region_id");
                if (regionIdStr.isEmpty()) {
                    logger.warn("Linha ignorada: ID da região pai não encontrado");
                    continue;
                }
                
                Long id = Long.parseLong(idStr);
                Long regionId = Long.parseLong(regionIdStr);
                
                Optional<RegionEntity> region = regionRepository.findById(regionId);
                if (!region.isPresent()) {
                    logger.warn("Região com ID {} não encontrada. Ignorando sub-região {}", regionId, id);
                    continue;
                }

                Optional<SubRegionEntity> existingSubRegion = subRegionRepository.findById(id);

                if (existingSubRegion.isPresent()) {
                    // Atualizar sub-região existente
                    SubRegionEntity subRegion = existingSubRegion.get();
                    subRegion.setName(getValueOrEmpty(line, headerMap, "name"));
                    subRegion.setRegionId(regionId);
                    subRegion.setWikiDataId(getValueOrEmpty(line, headerMap, "wikiDataId"));
                    subRegionRepository.save(subRegion);
                } else {
                    // Criar nova sub-região
                    SubRegionEntity subRegion = new SubRegionEntity();
                    subRegion.setId(id);
                    subRegion.setName(getValueOrEmpty(line, headerMap, "name"));
                    subRegion.setRegionId(regionId);
                    subRegion.setWikiDataId(getValueOrEmpty(line, headerMap, "wikiDataId"));
                    subRegionRepository.save(subRegion);
                    importCount++;
                }
            }
        }

        return importCount;
    }

    /**
     * Importa países a partir do arquivo CSV
     */
    @Transactional
    public int importCountries() throws IOException, CsvValidationException {
        String csvContent = getResourceAsString("csv/countries.csv");
        int importCount = 0;

        try (CSVReader csvReader = new CSVReader(new StringReader(csvContent))) {
            // Ler o cabeçalho
            String[] headers = csvReader.readNext();

            // Mapear os índices dos cabeçalhos
            Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerMap.put(headers[i], i);
            }

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                // Verificar se o país já existe
                String iso2 = getValueOrEmpty(line, headerMap, "iso2");
                Optional<CountryEntity> existingCountry = countryRepository.findByIso2(iso2);

                if (existingCountry.isPresent()) {
                    // Atualizar país existente
                    updateCountry(existingCountry.get(), line, headerMap);
                } else {
                    // Criar novo país
                    CountryEntity country = new CountryEntity();
                    populateCountry(country, line, headerMap);
                    countryRepository.save(country);
                    importCount++;
                }
            }
        }

        return importCount;
    }

    /**
     * Importa estados a partir do arquivo CSV
     */
    @Transactional
    public int importStates() throws IOException, CsvValidationException {
        String csvContent = getResourceAsString("csv/states.csv");
        int importCount = 0;

        try (CSVReader csvReader = new CSVReader(new StringReader(csvContent))) {
            // Ler o cabeçalho
            String[] headers = csvReader.readNext();

            // Mapear os índices dos cabeçalhos
            Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerMap.put(headers[i], i);
            }

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                // Obter o país associado
                String countryCode = getValueOrEmpty(line, headerMap, "country_code");
                Optional<CountryEntity> country = countryRepository.findByIso2(countryCode);

                if (country.isPresent()) {
                    String stateCode = getValueOrEmpty(line, headerMap, "state_code");
                    String countryId = getValueOrEmpty(line, headerMap, "country_id");

                    // Verificar se o estado já existe
                    Optional<StateEntity> existingState = stateRepository.findByStateCodeAndCountryId(
                            stateCode, Long.parseLong(countryId));

                    if (existingState.isPresent()) {
                        // Atualizar estado existente
                        updateState(existingState.get(), line, headerMap, country.get());
                    } else {
                        // Criar novo estado
                        StateEntity state = new StateEntity();
                        populateState(state, line, headerMap, country.get());
                        stateRepository.save(state);
                        importCount++;
                    }
                } else {
                    logger.warn("País não encontrado para o código: {}", countryCode);
                }
            }
        }

        return importCount;
    }

    /**
     * Importa cidades a partir do arquivo CSV
     */
    @Transactional
    public int importCities() throws IOException, CsvValidationException {
        String csvContent = getResourceAsString("csv/cities.csv");
        int importCount = 0;

        try (CSVReader csvReader = new CSVReader(new StringReader(csvContent))) {
            // Ler o cabeçalho
            String[] headers = csvReader.readNext();

            // Mapear os índices dos cabeçalhos
            Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerMap.put(headers[i], i);
            }

            String[] line;
            int batchSize = 500;
            int batchCount = 0;

            while ((line = csvReader.readNext()) != null) {
                // Obter o país e o estado associados
                String countryId = getValueOrEmpty(line, headerMap, "country_id");
                String stateId = getValueOrEmpty(line, headerMap, "state_id");

                Optional<CountryEntity> country = countryRepository.findById(Long.parseLong(countryId));
                Optional<StateEntity> state = stateRepository.findById(Long.parseLong(stateId));

                if (country.isPresent() && state.isPresent()) {
                    // Verificar se a cidade já existe
                    String cityName = getValueOrEmpty(line, headerMap, "name");
                    Optional<CityEntity> existingCity = cityRepository.findByNameAndStateId(
                            cityName, Long.parseLong(stateId));

                    if (existingCity.isPresent()) {
                        // Atualizar cidade existente
                        updateCity(existingCity.get(), line, headerMap, country.get(), state.get());
                    } else {
                        // Criar nova cidade
                        CityEntity city = new CityEntity();
                        populateCity(city, line, headerMap, country.get(), state.get());
                        cityRepository.save(city);
                        importCount++;
                    }

                    // Limpar o contexto de persistência periodicamente para economizar memória
                    if (++batchCount % batchSize == 0) {
                        stateRepository.flush();
                        entityManager.clear(); // Usar entityManager em vez de stateRepository.clear()
                        logger.debug("Processado lote de {} cidades. Total até agora: {}", batchSize, importCount);
                    }
                } else {
                    logger.warn("País ou estado não encontrado para os IDs: country={}, state={}", countryId, stateId);
                }
            }
        }

        return importCount;
    }

    /**
     * Importa dados de localidades de Cabo Verde a partir do arquivo CSV
     * @return Número de registros importados
     */
    public int importCapeVerdeLocations() throws IOException, CsvValidationException {
        logger.info("Iniciando importação de localidades de Cabo Verde");
        String csvResourcePath = "csv/cv/codigo-lugares-cv.csv";
        String csvContent = getResourceAsString(csvResourcePath);
        
        if (csvContent == null || csvContent.isEmpty()) {
            throw new IOException("Arquivo de localidades de Cabo Verde não encontrado: " + csvResourcePath);
        }
        
        CSVReader csvReader = new CSVReader(new StringReader(csvContent));
        String[] headerLine = csvReader.readNext(); // Ler cabeçalho
        
        if (headerLine == null) {
            throw new IOException("Arquivo CSV de localidades de Cabo Verde vazio ou inválido");
        }
        
        // Mapear os índices das colunas pelos nomes do cabeçalho
        Map<String, Integer> headerMap = new HashMap<>();
        for (int i = 0; i < headerLine.length; i++) {
            headerMap.put(headerLine[i].trim().toLowerCase(), i);
        }
        
        // Verificar colunas obrigatórias
        String[] requiredColumns = {"id", "codigo", "nivel", "pais", "ilha", "concelho", "freguesia", "zona", "lugar", "nome", "lat", "lng"};
        for (String column : requiredColumns) {
            if (!headerMap.containsKey(column)) {
                logger.warn("Coluna obrigatória '{}' não encontrada no CSV de localidades de Cabo Verde", column);
            }
        }
        
        int importedCount = 0;
        String[] line;
        
        // Cache para evitar consultas repetidas ao banco
        Map<Long, CapeVerdeLocationsEntity> existingLocationsMap = new HashMap<>();
        capeVerdeLocationsRepository.findAll().forEach(location -> 
            existingLocationsMap.put(location.getId(), location));
        
        logger.info("Encontrados {} registros existentes de localidades de Cabo Verde", existingLocationsMap.size());
        
        while ((line = csvReader.readNext()) != null) {
            try {
                Long id = getValueAsLong(line, headerMap, "id");
                if (id == null) {
                    logger.warn("ID inválido ou ausente na linha. Pulando...");
                    continue;
                }
                
                String codigo = getValueAsString(line, headerMap, "codigo");
                Integer nivel = getValueAsInteger(line, headerMap, "nivel");
                String pais = getValueAsString(line, headerMap, "pais");
                String ilha = getValueAsString(line, headerMap, "ilha");
                String concelho = getValueAsString(line, headerMap, "concelho");
                String freguesia = getValueAsString(line, headerMap, "freguesia");
                String zona = getValueAsString(line, headerMap, "zona");
                String lugar = getValueAsString(line, headerMap, "lugar");
                String nome = getValueAsString(line, headerMap, "nome");
                String lat = getValueAsString(line, headerMap, "lat");
                String lng = getValueAsString(line, headerMap, "lng");
                
                CapeVerdeLocationsEntity location;
                boolean isNew = !existingLocationsMap.containsKey(id);
                
                if (isNew) {
                    location = new CapeVerdeLocationsEntity();
                    location.setId(id);
                } else {
                    location = existingLocationsMap.get(id);
                }
                
                location.setCodigo(codigo);
                location.setNivel(nivel);
                location.setPais(pais);
                location.setIlha(ilha);
                location.setConcelho(concelho);
                location.setFreguesia(freguesia);
                location.setZona(zona);
                location.setLugar(lugar);
                location.setNome(nome);
                location.setLat(lat);
                location.setLng(lng);
                
                capeVerdeLocationsRepository.save(location);
                importedCount++;
                
                if (importedCount % 100 == 0) {
                    logger.info("Importados {} registros de localidades de Cabo Verde", importedCount);
                }
            } catch (Exception e) {
                logger.error("Erro ao processar linha de localidade de Cabo Verde: {}", e.getMessage(), e);
            }
        }
        
        csvReader.close();
        logger.info("Importação de localidades de Cabo Verde concluída. Total de registros importados: {}", importedCount);
        return importedCount;
    }
    
    private void populateCountry(CountryEntity country, String[] line, Map<String, Integer> headerMap) {
        country.setName(getValueOrEmpty(line, headerMap, "name"));
        country.setIso3(getValueOrEmpty(line, headerMap, "iso3"));
        country.setNumericCode(getValueOrEmpty(line, headerMap, "numeric_code"));
        country.setIso2(getValueOrEmpty(line, headerMap, "iso2"));
        country.setPhonecode(getValueOrEmpty(line, headerMap, "phonecode"));
        country.setCapital(getValueOrEmpty(line, headerMap, "capital"));
        country.setCurrency(getValueOrEmpty(line, headerMap, "currency"));
        country.setCurrencyName(getValueOrEmpty(line, headerMap, "currency_name"));
        country.setCurrencySymbol(getValueOrEmpty(line, headerMap, "currency_symbol"));
        country.setTld(getValueOrEmpty(line, headerMap, "tld"));
        country.setNativeName(getValueOrEmpty(line, headerMap, "native"));
        country.setRegion(getValueOrEmpty(line, headerMap, "region"));
        country.setSubregion(getValueOrEmpty(line, headerMap, "subregion"));
        
        // Verificar se existem os campos region_id e subregion_id no CSV
        String regionId = getValueOrEmpty(line, headerMap, "region_id");
        if (!regionId.isEmpty()) {
            try {
                country.setRegionId(Long.parseLong(regionId));
            } catch (NumberFormatException e) {
                logger.warn("Valor inválido para region_id: {}", regionId);
            }
        }
        
        String subregionId = getValueOrEmpty(line, headerMap, "subregion_id");
        if (!subregionId.isEmpty()) {
            try {
                country.setSubregionId(Long.parseLong(subregionId));
            } catch (NumberFormatException e) {
                logger.warn("Valor inválido para subregion_id: {}", subregionId);
            }
        }
        
        country.setLatitude(getValueOrEmpty(line, headerMap, "latitude"));
        country.setLongitude(getValueOrEmpty(line, headerMap, "longitude"));
        country.setEmoji(getValueOrEmpty(line, headerMap, "emoji"));
        country.setEmojiU(getValueOrEmpty(line, headerMap, "emojiU"));
        country.setCreatedAt(LocalDateTime.now());
        country.setUpdatedAt(LocalDateTime.now());
        country.setFlag(true);
        country.setWikiDataId(getValueOrEmpty(line, headerMap, "wikiDataId"));
    }

    private void updateCountry(CountryEntity country, String[] line, Map<String, Integer> headerMap) {
        country.setName(getValueOrEmpty(line, headerMap, "name"));
        country.setUpdatedAt(LocalDateTime.now());
        countryRepository.save(country);
    }

    private void populateState(StateEntity state, String[] line, Map<String, Integer> headerMap, CountryEntity country) {
        state.setName(getValueOrEmpty(line, headerMap, "name"));
        state.setCountry(country);
        state.setCountryCode(getValueOrEmpty(line, headerMap, "country_code"));
        state.setCountryName(country.getName());
        state.setFipsCode(getValueOrEmpty(line, headerMap, "fips_code"));
        state.setIso2(getValueOrEmpty(line, headerMap, "iso2"));
        state.setStateCode(getValueOrEmpty(line, headerMap, "state_code"));
        state.setType(getValueOrEmpty(line, headerMap, "type"));
        state.setLatitude(getValueOrEmpty(line, headerMap, "latitude"));
        state.setLongitude(getValueOrEmpty(line, headerMap, "longitude"));
        state.setCreatedAt(LocalDateTime.now());
        state.setUpdatedAt(LocalDateTime.now());
        state.setFlag(true);
        state.setWikiDataId(getValueOrEmpty(line, headerMap, "wikiDataId"));
    }

    private void updateState(StateEntity state, String[] line, Map<String, Integer> headerMap, CountryEntity country) {
        state.setName(getValueOrEmpty(line, headerMap, "name"));
        state.setCountryName(country.getName());
        state.setUpdatedAt(LocalDateTime.now());
        stateRepository.save(state);
    }

    private void populateCity(CityEntity city, String[] line, Map<String, Integer> headerMap, CountryEntity country, StateEntity state) {
        city.setName(getValueOrEmpty(line, headerMap, "name"));
        city.setState(state);
        city.setStateCode(getValueOrEmpty(line, headerMap, "state_code"));
        city.setStateName(state.getName());
        city.setCountry(country);
        city.setCountryCode(getValueOrEmpty(line, headerMap, "country_code"));
        city.setCountryName(country.getName());
        city.setLatitude(getValueOrEmpty(line, headerMap, "latitude"));
        city.setLongitude(getValueOrEmpty(line, headerMap, "longitude"));
        city.setCreatedAt(LocalDateTime.now());
        city.setUpdatedAt(LocalDateTime.now());
        city.setFlag(true);
        city.setWikiDataId(getValueOrEmpty(line, headerMap, "wikiDataId"));
    }

    private void updateCity(CityEntity city, String[] line, Map<String, Integer> headerMap, CountryEntity country, StateEntity state) {
        city.setName(getValueOrEmpty(line, headerMap, "name"));
        city.setStateName(state.getName());
        city.setCountryName(country.getName());
        city.setUpdatedAt(LocalDateTime.now());
        cityRepository.save(city);
    }

    private String getValueOrEmpty(String[] line, Map<String, Integer> headerMap, String columnName) {
        Integer index = headerMap.get(columnName);
        if (index != null && index < line.length) {
            return line[index];
        }
        return "";
    }

    /**
     * Obtém o conteúdo de um recurso como uma string.
     * Tenta múltiplos caminhos para encontrar o arquivo, funcionando em ambientes Docker e locais.
     *
     * @param resourcePath Caminho relativo para o recurso (ex: "csv/countries.csv")
     * @return O conteúdo do recurso como uma string
     * @throws IOException Se o recurso não puder ser encontrado ou lido
     */
    private String getResourceAsString(String resourcePath) throws IOException {
        logger.debug("Tentando carregar recurso: {}", resourcePath);
        
        // Lista de possíveis locais onde o arquivo pode estar
        String[] possiblePaths = {
            resourcePath,                                         // Caminho relativo no classpath
            "static/" + resourcePath,                             // Pasta static no classpath
            "src/main/resources/" + resourcePath,                 // Pasta resources no projeto
            "target/classes/" + resourcePath,                     // Pasta classes compiladas
            System.getProperty("user.dir") + "/" + resourcePath   // Caminho absoluto baseado no diretório de trabalho
        };
        
        // Tenta localizar o recurso em múltiplos locais
        for (String path : possiblePaths) {
            try {
                // Primeiro tenta como um recurso de classpath
                Resource resource = new ClassPathResource(path);
                if (resource.exists() && resource.isReadable()) {
                    logger.debug("Recurso encontrado no classpath: {}", path);
                    return readResourceContent(resource);
                }
                
                // Tenta como arquivo no sistema de arquivos
                File file = ResourceUtils.getFile("file:" + path);
                if (file.exists() && file.canRead()) {
                    logger.debug("Recurso encontrado no sistema de arquivos: {}", file.getAbsolutePath());
                    return Files.readString(file.toPath(), StandardCharsets.UTF_8);
                }
                
                // Tenta como caminho absoluto
                Path filePath = Paths.get(path);
                if (Files.exists(filePath) && Files.isReadable(filePath)) {
                    logger.debug("Recurso encontrado como caminho absoluto: {}", filePath.toAbsolutePath());
                    return Files.readString(filePath, StandardCharsets.UTF_8);
                }
            } catch (Exception e) {
                // Ignora exceções para tentar o próximo caminho
                logger.trace("Erro ao tentar carregar de {}: {}", path, e.getMessage());
            }
        }
        
        // Se chegou aqui, não conseguiu encontrar o arquivo em nenhum local
        String errorMsg = "Não foi possível encontrar o arquivo em nenhum local: " + resourcePath;
        logger.error(errorMsg);
        throw new IOException(errorMsg);
    }
    
    /**
     * Lê o conteúdo de um recurso e retorna como string
     */
    private String readResourceContent(Resource resource) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        }
    }
    
    // Métodos auxiliares para extrair valores das linhas CSV
    private String getValueAsString(String[] line, Map<String, Integer> headerMap, String columnName) {
        Integer index = headerMap.get(columnName);
        if (index == null || index >= line.length) {
            return "";
        }
        return line[index] != null ? line[index].trim() : "";
    }
    
    private Long getValueAsLong(String[] line, Map<String, Integer> headerMap, String columnName) {
        String value = getValueAsString(line, headerMap, columnName);
        if (value.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            logger.warn("Valor inválido para Long na coluna {}: {}", columnName, value);
            return null;
        }
    }
    
    private Integer getValueAsInteger(String[] line, Map<String, Integer> headerMap, String columnName) {
        String value = getValueAsString(line, headerMap, columnName);
        if (value.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("Valor inválido para Integer na coluna {}: {}", columnName, value);
            return null;
        }
    }
}
