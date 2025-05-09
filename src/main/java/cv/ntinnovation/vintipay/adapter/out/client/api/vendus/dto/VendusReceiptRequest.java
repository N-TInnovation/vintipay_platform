package cv.ntinnovation.vintipay.adapter.out.client.api.vendus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Classe que representa uma requisição para criar um recibo na API Vendus
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendusReceiptRequest {

    @JsonProperty("client")
    private VendusClientInfo client;
    
    @JsonProperty("items")
    private List<VendusReceiptItem> items;
    
    @JsonProperty("payment_method")
    private String paymentMethod;
    
    @JsonProperty("notes")
    private String notes;
    
    @JsonProperty("external_reference")
    private String externalReference;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendusClientInfo {
        @JsonProperty("fiscal_id")
        private String fiscalId;
        
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("email")
        private String email;
        
        @JsonProperty("address")
        private String address;
        
        @JsonProperty("postalcode")
        private String postalCode;
        
        @JsonProperty("city")
        private String city;
        
        @JsonProperty("country")
        private String country;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendusReceiptItem {
        @JsonProperty("reference")
        private String reference;
        
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("qty")
        private Integer quantity;
        
        @JsonProperty("price")
        private BigDecimal price;
        
        @JsonProperty("tax")
        private BigDecimal tax;
    }
}
