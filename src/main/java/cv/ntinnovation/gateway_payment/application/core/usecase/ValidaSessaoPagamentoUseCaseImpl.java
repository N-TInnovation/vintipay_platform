package cv.ntinnovation.gateway_payment.application.core.usecase;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.SessaoPagamento;
import cv.ntinnovation.gateway_payment.application.port.in.ValidaSessaoPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.out.BuscaSessaoPagamentoPort;
import cv.ntinnovation.gateway_payment.application.port.out.SalvaSessaoPagamentoPort;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidaSessaoPagamentoUseCaseImpl implements ValidaSessaoPagamentoUseCase {

    private final BuscaSessaoPagamentoPort buscaSessaoPagamentoPort;
    private final SalvaSessaoPagamentoPort salvaSessaoPagamentoPort;
    
    private static final String SESSION_COOKIE_NAME = "PAYMENT_SESSION_ID";

    @Override
    public Optional<SessaoPagamento> validarSessao(String reference, HttpServletRequest request) {
        // Primeiro verifica se existe uma sessão válida para a referência
        Optional<SessaoPagamento> sessaoByReference = buscaSessaoPagamentoPort.buscarSessaoValidaPorReferencia(reference);
        
        // Se não existir sessão válida para a referência, retorna vazio
        if (sessaoByReference.isEmpty()) {
            log.warn("Nenhuma sessão válida encontrada para a referência: {}", reference);
            return Optional.empty();
        }
        
        // Busca o cookie de sessão
        String sessionId = getSessionIdFromCookie(request);
        
        // Se não houver cookie de sessão, a validação falha
        if (sessionId == null) {
            log.warn("Cookie de sessão não encontrado para a referência: {}", reference);
            return Optional.empty();
        }
        
        // Verifica se o sessionId do cookie corresponde ao sessionId da sessão encontrada
        SessaoPagamento sessao = sessaoByReference.get();
        if (!sessao.getSessionId().equals(sessionId)) {
            log.warn("Session ID do cookie ({}) não corresponde ao Session ID da sessão ({}) para referência: {}", 
                    sessionId, sessao.getSessionId(), reference);
            return Optional.empty();
        }
        
        // Verifica se a sessão não expirou
        if (sessao.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Sessão expirada para referência: {}", reference);
            invalidarSessao(sessionId);
            return Optional.empty();
        }
        
        log.info("Sessão válida para referência: {}", reference);
        return Optional.of(sessao);
    }

    @Override
    @Transactional
    public void invalidarSessao(String sessionId) {
        buscaSessaoPagamentoPort.buscarPorSessionId(sessionId).ifPresent(sessao -> {
            sessao.setValid(false);
            salvaSessaoPagamentoPort.salvar(sessao);
            log.info("Sessão {} invalidada", sessionId);
        });
    }
    
    /**
     * Tarefa agendada para invalidar sessões expiradas
     */
    @Scheduled(fixedRate = 60000) // Executa a cada minuto
    @Transactional
    public void invalidarSessoesExpiradas() {
        LocalDateTime now = LocalDateTime.now();
        buscaSessaoPagamentoPort.buscarSessoesExpiradas().forEach(sessao -> {
            sessao.setValid(false);
            salvaSessaoPagamentoPort.salvar(sessao);
            log.info("Sessão expirada {} invalidada automaticamente", sessao.getSessionId());
        });
    }
    
    /**
     * Obtém o ID da sessão a partir do cookie
     */
    private String getSessionIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        
        return Arrays.stream(cookies)
                .filter(cookie -> SESSION_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
