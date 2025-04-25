package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.SessaoPagamento;

/**
 * Interface para salvar sessões de pagamento
 */
public interface SalvaSessaoPagamento {
    
    /**
     * Salva uma sessão de pagamento
     * 
     * @param sessaoPagamento Sessão de pagamento a ser salva
     * @return Sessão de pagamento salva
     */
    SessaoPagamento salvar(SessaoPagamento sessaoPagamento);
}
