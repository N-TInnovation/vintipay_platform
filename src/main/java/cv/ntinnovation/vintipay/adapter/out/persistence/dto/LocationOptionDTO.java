package cv.ntinnovation.vintipay.adapter.out.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar opções de localização com valor e texto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationOptionDTO {
    private String value;  // Valor a ser usado no campo value do option
    private String label;  // Texto a ser exibido no option
}
