package cv.ntinnovation.vintipay.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContaPagamentoResponse {
    
    private Long id;
    private String email;
    private String billAddrCity;
    private String billAddrCountry;
    private String billAddrLine1;
    private String billAddrPostCode;
    private LocalDateTime createdAt;
}
