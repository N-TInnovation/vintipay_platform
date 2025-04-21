package cv.ntinnovation.gateway_payment.application.port.out;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.SessaoPagamento;

import java.util.List;
import java.util.Optional;

public interface BuscaSessaoPagamentoPort {
    Optional<SessaoPagamento> buscarPorSessionId(String sessionId);
    Optional<SessaoPagamento> buscarSessaoValidaPorReferencia(String reference);
    List<SessaoPagamento> buscarPorClientId(String clientId);
    List<SessaoPagamento> buscarSessoesExpiradas();
}
