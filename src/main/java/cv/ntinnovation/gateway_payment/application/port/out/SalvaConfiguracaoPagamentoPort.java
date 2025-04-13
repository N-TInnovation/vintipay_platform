package cv.ntinnovation.gateway_payment.application.port.out;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;

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
