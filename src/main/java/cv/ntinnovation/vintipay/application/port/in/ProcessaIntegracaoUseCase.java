package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.VendusReceipt;

/**
 * Interface para processamento de recibos Vendus após pagamento
 */
public interface ProcessaIntegracaoUseCase<T> {
    
    /**
     * Processa um recibo Vendus para um pagamento bem-sucedido
     * @param pagamento o pagamento processado
     * @return a resposta da API Vendus ou null em caso de erro
     */
    T processar(ConfiguracaoPagamento configuracao, Pagamento pagamento);
}
