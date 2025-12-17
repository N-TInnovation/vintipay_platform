package cv.ntinnovation.vintipay.adapter.in.web.filter;

import cv.ntinnovation.vintipay.application.core.service.AmbienteService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro para aplicar o ambiente selecionado (HOMOLOGACAO/PRODUCAO) em todas as requisições.
 * Esse filtro coloca o ambiente atual como um atributo do request para fácil acesso
 * em outros componentes da aplicação.
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class AmbienteFilter extends OncePerRequestFilter {

    private final AmbienteService ambienteService;
    private static final String REQUEST_ATTRIBUTE_AMBIENTE = "ambiente";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            // Obtém o ambiente atual da sessão
            var ambiente = ambienteService.getAmbienteSelecionado(request);
            
            // Adiciona o ambiente como atributo do request para uso em outros componentes
            request.setAttribute(REQUEST_ATTRIBUTE_AMBIENTE, ambiente);
            
            log.debug("Ambiente aplicado ao request: {}", ambiente);
        } catch (Exception e) {
            log.error("Erro ao aplicar ambiente ao request", e);
        }
        
        // Continua a cadeia de filtros
        filterChain.doFilter(request, response);
    }
}
