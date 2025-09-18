package cv.ntinnovation.vintipay.adapter.in.web.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class EnvironmentProperties {
    @Value("${sisp.url.prod.3d}")
    private String submitPaymentProdFormUrl;

    @Value("${sisp.url.dev.3d}")
    private String submitPaymentDevFormUrl;

    @Value("${sisp.url.qld.3d}")
    private String submitPaymentQldFormUrl;

    @Value("${sisp.url.prod.normal}")
    private String submitPaymentNormalProdFormUrl;

    @Value("${sisp.url.dev.normal}")
    private String submitPaymentNormalDevFormUrl;

    @Value("${sisp.url.qld.normal}")
    private String submitPaymentNormalQldFormUrl;

    @Value("${sisp.url.prod.subscricao}")
    private String submitPaymentSubscricaoProdFormUrl;

    @Value("${sisp.url.dev.subscricao}")
    private String submitPaymentSubscricaoDevFormUrl;

    @Value("${sisp.url.qld.subscricao}")
    private String submitPaymentSubscricaoQldFormUrl;

    @Value("${sisp.url.prod.cobranca-recorrente}")
    private String submitPaymentCobrancaRecorrenteProdFormUrl;

    @Value("${sisp.url.dev.cobranca-recorrente}")
    private String submitPaymentCobrancaRecorrenteDevFormUrl;

    @Value("${sisp.url.qld.cobranca-recorrente}")
    private String submitPaymentCobrancaRecorrenteQldFormUrl;

    @Value("${sisp.url.prod.cancelamento-subscricao}")
    private String submitPaymentCancelamentoSubscricaoProdFormUrl;

    @Value("${sisp.url.dev.cancelamento-subscricao}")
    private String submitPaymentCancelamentoSubscricaoDevFormUrl;

    @Value("${sisp.url.qld.cancelamento-subscricao}")
    private String submitPaymentCancelamentoSubscricaoQldFormUrl;

    @Value("${sisp.checkout.return.url}")
    private String returnUrl;
}
