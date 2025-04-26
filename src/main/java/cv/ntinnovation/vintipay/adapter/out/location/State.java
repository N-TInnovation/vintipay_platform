package cv.ntinnovation.vintipay.adapter.out.location;

import cv.ntinnovation.vintipay.adapter.out.persistence.dto.StateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo para representar um estado.
 * Mantido para compatibilidade com o código existente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class State {
    private Long id;
    private String name;
    private String state_code;
    private Long country_id;
    private String country_code;
    private String country_name;
    
    /**
     * Converte um DTO de estado para o modelo State
     * @param dto DTO de estado
     * @return Modelo State
     */
    public static State fromDTO(StateDTO dto) {
        return new State(
            dto.getId(),
            dto.getName(),
            dto.getStateCode(),
            dto.getCountryId(),
            dto.getCountryCode(),
            dto.getCountryName()
        );
    }
}
