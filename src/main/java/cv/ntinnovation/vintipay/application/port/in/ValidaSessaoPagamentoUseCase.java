package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.SessaoPagamento;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface ValidaSessaoPagamentoUseCase {
    /**
     * Valida se existe uma sessão válida para a referência informada
     * @param reference Referência do pagamento
     * @param request Requisição HTTP para extrair informações como sessionId do cookie
     * @return Sessão de pagamento se válida, ou empty se inválida
     */
    Optional<SessaoPagamento> validarSessao(String reference, HttpServletRequest request);
    
    /**
     * Invalida uma sessão de pagamento
     * @param sessionId ID da sessão a ser invalidada
     */
    void invalidarSessao(String sessionId);

    Optional<SessaoPagamento> validarSessao(String sessionId);
}
