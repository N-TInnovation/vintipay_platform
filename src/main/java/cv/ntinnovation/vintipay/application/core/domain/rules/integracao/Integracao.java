package cv.ntinnovation.vintipay.application.core.domain.rules.integracao;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoIntegracoes;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;

public interface Integracao<T> {
    void setAplicacao(ConfiguracaoIntegracoes aplicacao);
    String getNome();

    void setService(T service);

    void processar(ConfiguracaoPagamento configuracao, Pagamento pagamento);
}
