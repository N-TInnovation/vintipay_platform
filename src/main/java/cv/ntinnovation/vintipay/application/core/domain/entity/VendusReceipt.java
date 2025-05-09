package cv.ntinnovation.vintipay.application.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
public class VendusReceipt {
    private Long id;

    private String number;

    private String date;

    private BigDecimal total;

    private String status;

    private String externalReference;

    private String url;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
