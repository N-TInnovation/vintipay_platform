package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.SessaoPagamento;
import cv.ntinnovation.vintipay.application.port.in.AtualizaStatusPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.ValidaSessaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaSessaoPagamentoPort;
import cv.ntinnovation.vintipay.application.port.out.SalvaSessaoPagamentoPort;
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

import static cv.ntinnovation.vintipay.adapter.in.web.utils.CookieUtil.CLIENT_ID;
import static cv.ntinnovation.vintipay.adapter.in.web.utils.CookieUtil.SESSION_COOKIE_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidaSessaoPagamentoUseCaseImpl implements ValidaSessaoPagamentoUseCase {

    private final BuscaSessaoPagamentoPort buscaSessaoPagamentoPort;
    private final SalvaSessaoPagamentoPort salvaSessaoPagamentoPort;
    private final AtualizaStatusPagamentoUseCase atualizaStatusPagamentoUseCase;

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
        String sessionId = getSessionIdFromCookie(request,SESSION_COOKIE_NAME);
        String clientId = getSessionIdFromCookie(request,CLIENT_ID);
        var sessao = sessaoByReference.get();

        if(clientId != null && !clientId.equals(sessao.getClientId())) {
            return Optional.empty();
        }

        if(!sessao.getSessionId().equals(sessionId)) {
            log.warn("Session ID do cookie ({}) não corresponde ao Session ID da sessão ({}) para referência: {}",
                    sessionId, sessao.getId(), reference);
            return Optional.empty();
        }

        // Se não houver cookie de sessão, a validação falha
        return sessaoByReference;
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

    @Override
    public Optional<SessaoPagamento> validarSessao(String sessionId) {
        var sessaoOpt = buscaSessaoPagamentoPort.buscarPorSessionId(sessionId);
        // Se não existir sessão válida para a referência, retorna vazio
        if (sessaoOpt.isEmpty()) {
            log.warn("Nenhuma sessão válida encontrada para a referência: {}", sessionId);
            return Optional.empty();
        }

        return sessaoOpt;
    }

    /**
     * Tarefa agendada para invalidar sessões expiradas
     */
    @Scheduled(fixedRate = 60000) // Executa a cada minuto
    @Transactional
    public void invalidarSessoesExpiradas() {
        buscaSessaoPagamentoPort.buscarSessoesExpiradas().forEach(sessao -> {
            sessao.setValid(false);
            salvaSessaoPagamentoPort.salvar(sessao);
            atualizaStatusPagamentoUseCase.expirarPagamentosPendentes();
            log.info("Sessão expirada {} invalidada automaticamente", sessao.getId());
        });
    }
    
    /**
     * Obtém o ID da sessão a partir do cookie
     */
    private String getSessionIdFromCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        
        return Arrays.stream(cookies)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
