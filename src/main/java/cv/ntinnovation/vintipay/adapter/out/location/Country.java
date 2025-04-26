package cv.ntinnovation.vintipay.adapter.out.location;

import cv.ntinnovation.vintipay.adapter.out.persistence.dto.CountryDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo para representar um país.
 * Mantido para compatibilidade com o código existente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Country {
    private Long id;
    private String name;
    private String iso2;
    private String iso3;
    private String phonecode;
    private String capital;
    private String currency;
    private String emoji;
    
    /**
     * Converte um DTO de país para o modelo Country
     * @param dto DTO de país
     * @return Modelo Country
     */
    public static Country fromDTO(CountryDTO dto) {
        return new Country(
            dto.getId(),
            dto.getName(),
            dto.getIso2(),
            dto.getIso3(),
            dto.getPhonecode(),
            dto.getCapital(),
            dto.getCurrency(),
            dto.getEmoji()
        );
    }
}
