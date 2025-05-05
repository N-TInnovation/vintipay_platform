package cv.ntinnovation.vintipay.adapter.out.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapeVerdeLocationDTO {
    private Long id;
    private String island;
    private String county;
    private String parish;
    private String zone;
    private String subzone;
    private String name;
}
