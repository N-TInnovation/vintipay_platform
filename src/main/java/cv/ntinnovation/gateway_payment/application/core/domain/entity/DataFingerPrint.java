package cv.ntinnovation.gateway_payment.application.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class DataFingerPrint {

    private String posAutCode;
    private String timestamp;
    private BigDecimal amount;
    private String merchantRef;
    private String merchantSession; 
    private String posID;    
    private String currency; 
    private String transactionCode; 
    private String entityCode;       
    private String referenceNumber;

}
