package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.SessaoPagamento;
import cv.ntinnovation.vintipay.application.port.in.CriaSessaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.ValidaSessaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaSessaoPagamentoPort;
import cv.ntinnovation.vintipay.application.port.out.SalvaSessaoPagamentoPort;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static cv.ntinnovation.vintipay.adapter.in.web.utils.GerarUtil.geraMerchantSession;

@Slf4j
@Service
@RequiredArgsConstructor
public class CriaSessaoPagamentoUseCaseImpl implements CriaSessaoPagamentoUseCase {

    private final SalvaSessaoPagamentoPort salvaSessaoPagamentoPort;
    private final BuscaSessaoPagamentoPort buscaSessaoPagamentoPort;
    
    @Value("${app.session.timeout-minutes:30}")
    private int sessionTimeoutMinutes;

    @Override
    @Transactional
    public SessaoPagamento criar(String reference, String sessionId, String clientId, HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(sessionTimeoutMinutes);

        Optional<SessaoPagamento> sessaoOpt = buscaSessaoPagamentoPort.buscarPorSessionId(sessionId);
        
        SessaoPagamento sessaoPagamento = sessaoOpt.orElse(SessaoPagamento.builder()
                .sessionId(sessionId)
                .referencia(reference)
                .clientId(clientId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .dataCriacao(now)
                .dataExpiracao(expiresAt)
                .valid(true)
                .build());

        sessaoPagamento.setValid(true);
        sessaoPagamento.setReferencia(reference);
        
        log.info("Criando nova sessão de pagamento para referência: {}, cliente: {}, IP: {}", 
                reference, clientId, ipAddress);
        
        return salvaSessaoPagamentoPort.salvar(sessaoPagamento);
    }
    
    /**
     * Obtém o endereço IP real do cliente, considerando proxies
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // Se houver múltiplos IPs (caso de múltiplos proxies), pega o primeiro
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
}
