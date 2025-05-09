package cv.ntinnovation.vintipay.application.core.domain.rules.integracao;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoAplicacao;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.port.out.VendusServicePort;
import lombok.Data;

@Data
public class VendusErp implements Integracao<VendusServicePort> {

    private VendusServicePort vendusServicePort;
    private ConfiguracaoAplicacao aplicacao;

    @Override
    public String getNome() {
        return "vendus";
    }

    @Override
    public void setService(VendusServicePort service) {
        this.vendusServicePort = service;
    }

    @Override
    public void processar(ConfiguracaoPagamento configuracao, Pagamento pagamento) {
        if (aplicacao.isActive() && configuracao.getApiKey() != null)
            vendusServicePort.criarRecibo(aplicacao.getApiKey(), pagamento);
    }
}
