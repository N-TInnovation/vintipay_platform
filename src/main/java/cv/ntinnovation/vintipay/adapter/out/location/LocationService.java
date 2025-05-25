package cv.ntinnovation.vintipay.adapter.out.location;

import cv.ntinnovation.vintipay.adapter.out.persistence.dto.CityDTO;
import cv.ntinnovation.vintipay.adapter.out.persistence.dto.CountryDTO;
import cv.ntinnovation.vintipay.adapter.out.persistence.dto.CapeVerdeLocationDTO;
import cv.ntinnovation.vintipay.adapter.out.persistence.dto.StateDTO;
import cv.ntinnovation.vintipay.adapter.out.persistence.dto.LocationOptionDTO;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.CityEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.CountryEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.CapeVerdeLocationsEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.StateEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.CityRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.CountryRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.CapeVerdeLocationsRepository;
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
    private final CapeVerdeLocationsRepository capeVerdeLocationsRepository;

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
     * Obtém a lista de estados de um país pelo código numérico
     * @param numericCode Código numérico do país
     * @return Lista de estados
     */
    public List<State> getStatesByCountryNumericCode(String numericCode) {
        try {
            CountryEntity country = countryRepository.findByNumericCode(numericCode).orElse(null);
            if (country == null) {
                log.warn("País com código numérico {} não encontrado", numericCode);
                return Collections.emptyList();
            }
            
            return stateRepository.findByCountryIso2OrderByNameAsc(country.getIso2()).stream()
                    .map(this::mapToStateDTO)
                    .map(State::fromDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Erro ao obter lista de estados para o país com código numérico {}", numericCode, e);
            return Collections.emptyList();
        }
    }

    /**
     * Obtém a lista de cidades de um estado usando o código numérico do país
     * @param numericCode Código numérico do país
     * @param stateCode Código do estado
     * @return Lista de cidades
     */
    public List<City> getCitiesByStateNumericCode(String numericCode, String stateCode) {
        try {
            CountryEntity country = countryRepository.findByNumericCode(numericCode).orElse(null);
            if (country == null) {
                log.warn("País com código numérico {} não encontrado", numericCode);
                return Collections.emptyList();
            }
            
            return cityRepository.findByCountryIso2AndStateStateCodeOrderByNameAsc(country.getIso2(), stateCode).stream()
                    .map(this::mapToCityDTO)
                    .map(City::fromDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Erro ao obter lista de cidades para o país com código numérico {} e estado {}", numericCode, stateCode, e);
            return Collections.emptyList();
        }
    }

    /**
     * Obtém a lista de todas as ilhas de Cabo Verde
     * @return Lista de ilhas
     */
    public List<String> getAllCapeVerdeIslands() {
        try {
            return capeVerdeLocationsRepository.findDistinctIlha();
        } catch (Exception e) {
            log.error("Erro ao obter lista de ilhas de Cabo Verde", e);
            return Collections.emptyList();
        }
    }

    /**
     * Obtém a lista de concelhos de uma ilha específica
     * @param ilha Nome da ilha
     * @return Lista de concelhos
     */
    public List<String> getCapeVerdeCountiesByIsland(String ilha) {
        try {
            return capeVerdeLocationsRepository.findDistinctConcelhoByIlha(ilha);
        } catch (Exception e) {
            log.error("Erro ao obter lista de concelhos para a ilha {}", ilha, e);
            return Collections.emptyList();
        }
    }

    /**
     * Obtém a lista de freguesias de um concelho específico
     * @param concelho Nome do concelho
     * @return Lista de freguesias
     */
    public List<String> getCapeVerdeParishesByCounty(String concelho) {
        try {
            return capeVerdeLocationsRepository.findDistinctFreguesiaByConcelho(concelho);
        } catch (Exception e) {
            log.error("Erro ao obter lista de freguesias para o concelho {}", concelho, e);
            return Collections.emptyList();
        }
    }

    /**
     * Obtém a lista de zonas de uma freguesia específica
     * @param freguesia Nome da freguesia
     * @return Lista de zonas
     */
    public List<String> getCapeVerdeZonesByParish(String freguesia) {
        try {
            return capeVerdeLocationsRepository.findDistinctZonaByFreguesia(freguesia);
        } catch (Exception e) {
            log.error("Erro ao obter lista de zonas para a freguesia {}", freguesia, e);
            return Collections.emptyList();
        }
    }

    /**
     * Obtém a lista de subzonas de uma zona específica
     * @param zona Nome da zona
     * @return Lista de subzonas
     */
    public List<String> getCapeVerdeSubzonesByZone(String zona) {
        try {
            return capeVerdeLocationsRepository.findDistinctLugarByZona(zona);
        } catch (Exception e) {
            log.error("Erro ao obter lista de subzonas para a zona {}", zona, e);
            return Collections.emptyList();
        }
    }

    /**
     * Obtém a lista de todas as ilhas de Cabo Verde com seus nomes para exibição
     * @return Lista de opções de ilha (valor/label)
     */
    public List<LocationOptionDTO> getAllCapeVerdeIslandsWithNames() {
        try {
            return capeVerdeLocationsRepository.findDistinctIlhaWithNames();
        } catch (Exception e) {
            log.error("Erro ao obter lista de ilhas de Cabo Verde com nomes", e);
            return Collections.emptyList();
        }
    }

    /**
     * Obtém a lista de concelhos de uma ilha específica com seus nomes para exibição
     * @param ilha Nome da ilha
     * @return Lista de opções de concelho (valor/label)
     */
    public List<LocationOptionDTO> getCapeVerdeCountiesByIslandWithNames(String ilha) {
        try {
            return capeVerdeLocationsRepository.findDistinctConcelhoByIlhaWithNames(ilha);
        } catch (Exception e) {
            log.error("Erro ao obter lista de concelhos para a ilha {} com nomes", ilha, e);
            return Collections.emptyList();
        }
    }

    /**
     * Obtém a lista de freguesias de um concelho específico com seus nomes para exibição
     * @param concelho Nome do concelho
     * @return Lista de opções de freguesia (valor/label)
     */
    public List<LocationOptionDTO> getCapeVerdeParishesByCountyWithNames(String concelho) {
        try {
            return capeVerdeLocationsRepository.findDistinctFreguesiaByConcelhoWithNames(concelho);
        } catch (Exception e) {
            log.error("Erro ao obter lista de freguesias para o concelho {} com nomes", concelho, e);
            return Collections.emptyList();
        }
    }

    /**
     * Obtém a lista de zonas de uma freguesia específica com seus nomes para exibição
     * @param freguesia Nome da freguesia
     * @return Lista de opções de zona (valor/label)
     */
    public List<LocationOptionDTO> getCapeVerdeZonesByParishWithNames(String freguesia) {
        try {
            return capeVerdeLocationsRepository.findDistinctZonaByFreguesiaWithNames(freguesia);
        } catch (Exception e) {
            log.error("Erro ao obter lista de zonas para a freguesia {} com nomes", freguesia, e);
            return Collections.emptyList();
        }
    }

    /**
     * Obtém a lista de subzonas de uma zona específica com seus nomes para exibição
     * @param zona Nome da zona
     * @return Lista de opções de subzona (valor/label)
     */
    public List<LocationOptionDTO> getCapeVerdeSubzonesByZoneWithNames(String zona) {
        try {
            return capeVerdeLocationsRepository.findDistinctLugarByZonaWithNames(zona);
        } catch (Exception e) {
            log.error("Erro ao obter lista de subzonas para a zona {} com nomes", zona, e);
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
        dto.setNumericCode(entity.getNumericCode());
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

    /**
     * Mapeia uma entidade de localização de Cabo Verde para DTO
     * @param entity Entidade de localização de Cabo Verde
     * @return DTO de localização de Cabo Verde
     */
    private CapeVerdeLocationDTO mapToCapeVerdeLocationDTO(CapeVerdeLocationsEntity entity) {
        CapeVerdeLocationDTO dto = new CapeVerdeLocationDTO();
        dto.setId(entity.getId());
        dto.setCodigo(entity.getCodigo());
        dto.setNivel(entity.getNivel());
        dto.setPais(entity.getPais());
        dto.setIlha(entity.getIlha());
        dto.setConcelho(entity.getConcelho());
        dto.setFreguesia(entity.getFreguesia());
        dto.setZona(entity.getZona());
        dto.setLugar(entity.getLugar());
        dto.setNome(entity.getNome());
        return dto;
    }
}
