package cv.ntinnovation.vintipay.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GraficoPagamentoDTO {
    private List<String> labels;
    private Map<String, Map<String, Long>> dados;
}
