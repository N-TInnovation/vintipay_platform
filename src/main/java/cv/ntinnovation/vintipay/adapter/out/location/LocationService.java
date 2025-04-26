package cv.ntinnovation.vintipay.adapter.out.location;

import cv.ntinnovation.vintipay.adapter.out.persistence.dto.CityDTO;
import cv.ntinnovation.vintipay.adapter.out.persistence.dto.CountryDTO;
import cv.ntinnovation.vintipay.adapter.out.persistence.dto.StateDTO;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.CityEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.CountryEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.StateEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.CityRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.CountryRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço para obter dados de localização (países, estados, cidades)
 * diretamente do banco de dados.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;

    /**
     * Obtém a lista de todos os países
     * @return Lista de países
     */
    public List<Country> getAllCountries() {
        try {
            return countryRepository.findAllByOrderByNameAsc().stream()
                    .map(this::mapToCountryDTO)
                    .map(Country::fromDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Erro ao obter lista de países", e);
            return Collections.emptyList();
        }
    }

    /**
     * Obtém a lista de estados de um país
     * @param countryCode Código ISO2 do país
     * @return Lista de estados
     */
    public List<State> getStatesByCountry(String countryCode) {
        try {
            return stateRepository.findByCountryIso2OrderByNameAsc(countryCode).stream()
                    .map(this::mapToStateDTO)
                    .map(State::fromDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Erro ao obter lista de estados para o país {}", countryCode, e);
            return Collections.emptyList();
        }
    }

    /**
     * Obtém a lista de cidades de um estado
     * @param countryCode Código ISO2 do país
     * @param stateCode Código do estado
     * @return Lista de cidades
     */
    public List<City> getCitiesByState(String countryCode, String stateCode) {
        try {
            return cityRepository.findByCountryIso2AndStateStateCodeOrderByNameAsc(countryCode, stateCode).stream()
                    .map(this::mapToCityDTO)
                    .map(City::fromDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Erro ao obter lista de cidades para o país {} e estado {}", countryCode, stateCode, e);
            return Collections.emptyList();
        }
    }

    /**
     * Obtém a lista de DTOs de todos os países
     * @return Lista de DTOs de países
     */
    public List<CountryDTO> getAllCountriesDTO() {
        try {
            return countryRepository.findAllByOrderByNameAsc().stream()
                    .map(this::mapToCountryDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Erro ao obter lista de DTOs de países", e);
            return Collections.emptyList();
        }
    }

    /**
     * Obtém a lista de DTOs de estados de um país
     * @param countryCode Código ISO2 do país
     * @return Lista de DTOs de estados
     */
    public List<StateDTO> getStatesByCountryDTO(String countryCode) {
        try {
            return stateRepository.findByCountryIso2OrderByNameAsc(countryCode).stream()
                    .map(this::mapToStateDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Erro ao obter lista de DTOs de estados para o país {}", countryCode, e);
            return Collections.emptyList();
        }
    }

    /**
     * Obtém a lista de DTOs de cidades de um estado
     * @param countryCode Código ISO2 do país
     * @param stateCode Código do estado
     * @return Lista de DTOs de cidades
     */
    public List<CityDTO> getCitiesByStateDTO(String countryCode, String stateCode) {
        try {
            return cityRepository.findByCountryIso2AndStateStateCodeOrderByNameAsc(countryCode, stateCode).stream()
                    .map(this::mapToCityDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Erro ao obter lista de DTOs de cidades para o país {} e estado {}", countryCode, stateCode, e);
            return Collections.emptyList();
        }
    }

    /**
     * Mapeia uma entidade de país para DTO
     * @param entity Entidade de país
     * @return DTO de país
     */
    private CountryDTO mapToCountryDTO(CountryEntity entity) {
        CountryDTO dto = new CountryDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setIso2(entity.getIso2());
        dto.setIso3(entity.getIso3());
        dto.setPhonecode(entity.getPhonecode());
        dto.setCapital(entity.getCapital());
        dto.setCurrency(entity.getCurrency());
        dto.setEmoji(entity.getEmoji());
        return dto;
    }

    /**
     * Mapeia uma entidade de estado para DTO
     * @param entity Entidade de estado
     * @return DTO de estado
     */
    private StateDTO mapToStateDTO(StateEntity entity) {
        StateDTO dto = new StateDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setStateCode(entity.getStateCode());
        dto.setCountryId(entity.getCountry().getId());
        dto.setCountryCode(entity.getCountryCode());
        dto.setCountryName(entity.getCountry().getName());
        return dto;
    }

    /**
     * Mapeia uma entidade de cidade para DTO
     * @param entity Entidade de cidade
     * @return DTO de cidade
     */
    private CityDTO mapToCityDTO(CityEntity entity) {
        CityDTO dto = new CityDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setStateId(entity.getState().getId());
        dto.setStateCode(entity.getStateCode());
        dto.setStateName(entity.getState().getName());
        dto.setCountryId(entity.getCountry().getId());
        dto.setCountryCode(entity.getCountryCode());
        dto.setCountryName(entity.getCountry().getName());
        return dto;
    }
}
