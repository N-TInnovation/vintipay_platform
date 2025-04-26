package cv.ntinnovation.vintipay.adapter.out.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferência de dados de estado
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StateDTO {
    private Long id;
    private String name;
    private String stateCode;
    private Long countryId;
    private String countryCode;
    private String countryName;
}
