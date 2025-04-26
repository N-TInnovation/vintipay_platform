package cv.ntinnovation.vintipay.adapter.out.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferência de dados de país
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryDTO {
    private Long id;
    private String name;
    private String iso2;
    private String iso3;
    private String phonecode;
    private String capital;
    private String currency;
    private String emoji;
}
