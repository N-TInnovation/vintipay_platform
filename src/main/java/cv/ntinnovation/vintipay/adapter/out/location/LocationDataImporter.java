package cv.ntinnovation.vintipay.adapter.out.location;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import cv.ntinnovation.vintipay.adapter.out.location.util.CsvResourceLoader;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.CityEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.CountryEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.RegionEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.StateEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.SubRegionEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.CityRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.CountryRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.RegionRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.StateRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.SubRegionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LocationDataImporter {

    private static final Logger logger = LoggerFactory.getLogger(LocationDataImporter.class);

    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final RegionRepository regionRepository;
    private final SubRegionRepository subRegionRepository;
    private final CsvResourceLoader csvResourceLoader;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public int importRegions() throws IOException, CsvValidationException {
        String csvContent = csvResourceLoader.loadAsString("csv/regions.csv");
        int importCount = 0;

        try (CSVReader csvReader = new CSVReader(new StringReader(csvContent))) {
            Map<String, Integer> headerMap = buildHeaderMap(csvReader.readNext());

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                String idStr = getValueOrEmpty(line, headerMap, "id");
                if (idStr.isEmpty()) {
                    logger.warn("Linha ignorada: ID da região não encontrado");
                    continue;
                }

                Long id = Long.parseLong(idStr);
                Optional<RegionEntity> existingRegion = regionRepository.findById(id);

                if (existingRegion.isPresent()) {
                    RegionEntity region = existingRegion.get();
                    region.setName(getValueOrEmpty(line, headerMap, "name"));
                    region.setWikiDataId(getValueOrEmpty(line, headerMap, "wikiDataId"));
                    regionRepository.save(region);
                } else {
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

    @Transactional
    public int importSubRegions() throws IOException, CsvValidationException {
        String csvContent = csvResourceLoader.loadAsString("csv/subregions.csv");
        int importCount = 0;

        try (CSVReader csvReader = new CSVReader(new StringReader(csvContent))) {
            Map<String, Integer> headerMap = buildHeaderMap(csvReader.readNext());

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                String idStr = getValueOrEmpty(line, headerMap, "id");
                String regionIdStr = getValueOrEmpty(line, headerMap, "region_id");

                if (idStr.isEmpty() || regionIdStr.isEmpty()) {
                    logger.warn("Linha ignorada: ID da sub-região ou região pai não encontrado");
                    continue;
                }

                Long id = Long.parseLong(idStr);
                Long regionId = Long.parseLong(regionIdStr);

                Optional<RegionEntity> region = regionRepository.findById(regionId);
                if (region.isEmpty()) {
                    logger.warn("Região com ID {} não encontrada. Ignorando sub-região {}", regionId, id);
                    continue;
                }

                Optional<SubRegionEntity> existingSubRegion = subRegionRepository.findById(id);
                if (existingSubRegion.isPresent()) {
                    SubRegionEntity subRegion = existingSubRegion.get();
                    subRegion.setName(getValueOrEmpty(line, headerMap, "name"));
                    subRegion.setRegion(region.get());
                    subRegion.setWikiDataId(getValueOrEmpty(line, headerMap, "wikiDataId"));
                    subRegionRepository.save(subRegion);
                } else {
                    SubRegionEntity subRegion = new SubRegionEntity();
                    subRegion.setId(id);
                    subRegion.setName(getValueOrEmpty(line, headerMap, "name"));
                    subRegion.setRegion(region.get());
                    subRegion.setWikiDataId(getValueOrEmpty(line, headerMap, "wikiDataId"));
                    subRegionRepository.save(subRegion);
                    importCount++;
                }
            }
        }

        return importCount;
    }

    @Transactional
    public int importCountries() throws IOException, CsvValidationException {
        String csvContent = csvResourceLoader.loadAsString("csv/countries.csv");
        int importCount = 0;

        try (CSVReader csvReader = new CSVReader(new StringReader(csvContent))) {
            Map<String, Integer> headerMap = buildHeaderMap(csvReader.readNext());

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                String iso2 = getValueOrEmpty(line, headerMap, "iso2");
                if (iso2.isEmpty() || countryRepository.findByIso2(iso2).isPresent()) {
                    continue;
                }

                CountryEntity country = new CountryEntity();
                populateCountry(country, line, headerMap);
                countryRepository.save(country);
                importCount++;
            }
        }

        return importCount;
    }

    @Transactional
    public int importStates() throws IOException, CsvValidationException {
        String csvContent = csvResourceLoader.loadAsString("csv/states.csv");
        int importCount = 0;

        try (CSVReader csvReader = new CSVReader(new StringReader(csvContent))) {
            Map<String, Integer> headerMap = buildHeaderMap(csvReader.readNext());

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                String countryCode = getValueOrEmpty(line, headerMap, "country_code");
                Optional<CountryEntity> country = countryRepository.findByIso2(countryCode);

                if (country.isEmpty()) {
                    logger.warn("País não encontrado para o código: {}", countryCode);
                    continue;
                }

                String stateCode = getValueOrEmpty(line, headerMap, "state_code");
                if (stateRepository.findByStateCodeAndCountryId(stateCode, country.get().getId()).isEmpty()) {
                    StateEntity state = new StateEntity();
                    populateState(state, line, headerMap, country.get());
                    stateRepository.save(state);
                    importCount++;
                }
            }
        }

        return importCount;
    }

    @Transactional
    public int importCities() throws IOException, CsvValidationException {
        String csvContent = csvResourceLoader.loadAsString("csv/cities.csv");
        int importCount = 0;
        int batchCount = 0;
        final int batchSize = 500;

        try (CSVReader csvReader = new CSVReader(new StringReader(csvContent))) {
            Map<String, Integer> headerMap = buildHeaderMap(csvReader.readNext());

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                String countryCode = getValueOrEmpty(line, headerMap, "country_code");
                String stateCode = getValueOrEmpty(line, headerMap, "state_code");

                Optional<CountryEntity> country = countryRepository.findByIso2(countryCode);
                if (country.isEmpty()) {
                    logger.warn("País não encontrado para o código: {}", countryCode);
                    continue;
                }

                Optional<StateEntity> state = stateRepository.findByStateCodeAndCountry_Id(stateCode, country.get().getId());
                if (state.isEmpty()) {
                    logger.warn("Estado não encontrado para country={}, state={}", countryCode, stateCode);
                    continue;
                }

                String cityName = getValueOrEmpty(line, headerMap, "name");
                if (cityRepository.findByNameAndStateCodeAndCountryCode(cityName, state.get().getStateCode(), country.get().getIso2()).isPresent()) {
                    continue;
                }

                CityEntity city = new CityEntity();
                populateCity(city, line, headerMap, country.get(), state.get());
                cityRepository.save(city);
                importCount++;

                if (++batchCount % batchSize == 0) {
                    stateRepository.flush();
                    entityManager.clear();
                    logger.debug("Processado lote de {} cidades. Total até agora: {}", batchSize, importCount);
                }
            }
        }

        return importCount;
    }

    private Map<String, Integer> buildHeaderMap(String[] headers) {
        Map<String, Integer> headerMap = new HashMap<>();
        if (headers != null) {
            for (int i = 0; i < headers.length; i++) {
                headerMap.put(headers[i], i);
            }
        }
        return headerMap;
    }

    private String getValueOrEmpty(String[] line, Map<String, Integer> headerMap, String columnName) {
        Integer index = headerMap.get(columnName);
        if (index != null && index < line.length) {
            return line[index];
        }
        return "";
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
        country.setTimezones(getValueOrEmpty(line, headerMap, "timezones"));
        country.setNativeName(getValueOrEmpty(line, headerMap, "native"));

        String regionId = getValueOrEmpty(line, headerMap, "region_id");
        if (!regionId.isEmpty()) {
            try {
                regionRepository.findById(Long.parseLong(regionId)).ifPresent(country::setRegion);
            } catch (NumberFormatException e) {
                logger.warn("Valor inválido para region_id: {}", regionId);
            }
        }

        String subregionId = getValueOrEmpty(line, headerMap, "subregion_id");
        if (!subregionId.isEmpty()) {
            try {
                subRegionRepository.findById(Long.parseLong(subregionId)).ifPresent(country::setSubRegion);
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

    private void populateState(StateEntity state, String[] line, Map<String, Integer> headerMap, CountryEntity country) {
        state.setName(getValueOrEmpty(line, headerMap, "name"));
        state.setCountry(country);
        state.setCountryCode(country.getIso2());
        state.setCountryName(country.getName());
        state.setStateCode(getValueOrEmpty(line, headerMap, "state_code"));
        state.setType(getValueOrEmpty(line, headerMap, "type"));
        state.setLatitude(getValueOrEmpty(line, headerMap, "latitude"));
        state.setLongitude(getValueOrEmpty(line, headerMap, "longitude"));
        state.setCreatedAt(LocalDateTime.now());
        state.setUpdatedAt(LocalDateTime.now());
    }

    private void populateCity(CityEntity city, String[] line, Map<String, Integer> headerMap, CountryEntity country, StateEntity state) {
        city.setName(getValueOrEmpty(line, headerMap, "name"));
        city.setState(state);
        city.setStateCode(state.getStateCode());
        city.setStateName(state.getName());
        city.setCountry(country);
        city.setCountryCode(country.getIso2());
        city.setCountryName(country.getName());
        city.setLatitude(getValueOrEmpty(line, headerMap, "latitude"));
        city.setLongitude(getValueOrEmpty(line, headerMap, "longitude"));
        city.setCreatedAt(LocalDateTime.now());
        city.setUpdatedAt(LocalDateTime.now());
        city.setWikiDataId(getValueOrEmpty(line, headerMap, "wikiDataId"));
    }
}
