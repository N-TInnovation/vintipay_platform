package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.SessaoPagamento;
import jakarta.servlet.http.HttpServletRequest;

public interface CriaSessaoPagamentoUseCase {
    /**
     * Cria uma nova sessão de pagamento
     * @param reference Referência do pagamento
     * @param clientId ID do cliente
     * @param request Requisição HTTP para extrair informações como IP e User-Agent
     * @return Sessão de pagamento criada
     */
    SessaoPagamento criar(String reference, String clientId, HttpServletRequest request);
}
