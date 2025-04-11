package cv.ntinnovation.gateway_payment.application.core.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class PlanoAssinatura {
    private String name;
    private List<Produto> produtoList;
    private BigDecimal price;
    private String recorenciaPagamento;
    private String subscriptionItemId;
}
