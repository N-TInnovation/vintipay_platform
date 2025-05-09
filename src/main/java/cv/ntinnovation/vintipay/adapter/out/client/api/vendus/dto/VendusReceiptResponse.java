package cv.ntinnovation.vintipay.adapter.out.client.api.vendus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Classe que representa a resposta da API Vendus ao criar um recibo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendusReceiptResponse {

    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("number")
    private String number;
    
    @JsonProperty("date")
    private String date;
    
    @JsonProperty("total")
    private BigDecimal total;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("external_reference")
    private String externalReference;
    
    @JsonProperty("url")
    private String url;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
