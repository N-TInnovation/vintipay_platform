package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.SubscricaoPagamento;
import java.util.Optional;

public interface SalvaSubscricaoPagamentoPort {
    
    /**
     * Salva uma subscrição de pagamento
     * 
     * @param subscricaoPagamento a subscrição a ser salva
     * @return a subscrição salva com o ID gerado
     */
    SubscricaoPagamento salvar(SubscricaoPagamento subscricaoPagamento);
    
    /**
     * Busca uma subscrição pelo ID da intenção de pagamento
     * 
     * @param intencaoPagamentoId o ID da intenção de pagamento
     * @return a subscrição encontrada, ou empty se não existir
     */
    Optional<SubscricaoPagamento> buscarPorIntencaoPagamentoId(Long intencaoPagamentoId);
}
