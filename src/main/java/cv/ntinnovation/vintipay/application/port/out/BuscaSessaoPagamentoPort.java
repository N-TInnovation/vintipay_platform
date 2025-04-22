package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.SessaoPagamento;

import java.util.List;
import java.util.Optional;

public interface BuscaSessaoPagamentoPort {
    Optional<SessaoPagamento> buscarPorSessionId(String sessionId);
    Optional<SessaoPagamento> buscarSessaoValidaPorReferencia(String reference);
    List<SessaoPagamento> buscarPorClientId(String clientId);
    List<SessaoPagamento> buscarSessoesExpiradas();
}
