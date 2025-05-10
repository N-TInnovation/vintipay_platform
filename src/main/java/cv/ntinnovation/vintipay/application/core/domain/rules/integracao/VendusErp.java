package cv.ntinnovation.vintipay.application.core.domain.rules.integracao;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoIntegracoes;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.port.out.VendusServicePort;
import lombok.Data;

import java.util.List;

@Data
public class VendusErp implements Integracao<VendusServicePort> {

    private VendusServicePort vendusServicePort;
    private ConfiguracaoIntegracoes aplicacao;
    private String apiUrl;
    private List<String> versoesSuportadas;
    private String logoUrl;

    public VendusErp() {
        this.versoesSuportadas = List.of("1.0", "1.1", "1.2");
        this.apiUrl = "https://www.vendus.cv/ws/";
        this.logoUrl = "https://code-hack.vendus.cv/m-img/logo_cegid_vendus.svg";
    }

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
    
    @Override
    public void setAplicacao(ConfiguracaoIntegracoes aplicacao) {
        this.aplicacao = aplicacao;
    }
}
