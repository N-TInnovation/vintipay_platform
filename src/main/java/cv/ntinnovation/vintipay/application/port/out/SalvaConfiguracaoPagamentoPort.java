package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;

/**
 * Interface para salvar configurações de pagamento
 */
public interface SalvaConfiguracaoPagamentoPort {
    /**
     * Salva uma configuração de pagamento
     * 
     * @param configuracaoPagamento a configuração de pagamento a ser salva
     * @return a configuração de pagamento salva
     */
    ConfiguracaoPagamento salvar(ConfiguracaoPagamento configuracaoPagamento);
}
