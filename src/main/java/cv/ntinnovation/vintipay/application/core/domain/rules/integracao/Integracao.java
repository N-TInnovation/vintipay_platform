package cv.ntinnovation.vintipay.application.core.domain.rules.integracao;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoAplicacao;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;

public interface Integracao<T> {
    void setAplicacao(ConfiguracaoAplicacao aplicacao);
    String getNome();

    void setService(T service);

    void processar(ConfiguracaoPagamento configuracao, Pagamento pagamento);
}
