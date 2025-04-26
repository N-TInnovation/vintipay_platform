package cv.ntinnovation.vintipay.adapter.out.location;

import cv.ntinnovation.vintipay.adapter.out.persistence.dto.CityDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo para representar uma cidade.
 * Mantido para compatibilidade com o código existente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class City {
    private Long id;
    private String name;
    private Long state_id;
    private String state_code;
    private String state_name;
    private Long country_id;
    private String country_code;
    private String country_name;
    
    /**
     * Converte um DTO de cidade para o modelo City
     * @param dto DTO de cidade
     * @return Modelo City
     */
    public static City fromDTO(CityDTO dto) {
        return new City(
            dto.getId(),
            dto.getName(),
            dto.getStateId(),
            dto.getStateCode(),
            dto.getStateName(),
            dto.getCountryId(),
            dto.getCountryCode(),
            dto.getCountryName()
        );
    }
}
