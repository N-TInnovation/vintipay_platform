package cv.ntinnovation.vintipay.adapter.out.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferência de dados de cidade
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityDTO {
    private Long id;
    private String name;
    private Long stateId;
    private String stateCode;
    private String stateName;
    private Long countryId;
    private String countryCode;
    private String countryName;
}
