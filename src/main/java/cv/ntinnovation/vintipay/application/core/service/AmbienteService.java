package cv.ntinnovation.vintipay.application.core.service;

import cv.ntinnovation.vintipay.application.core.domain.enums.AmbienteEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Serviço para gerenciar a seleção de ambiente (HOMOLOGACAO/PRODUCAO) em todo o sistema
 * Armazena o ambiente selecionado na sessão do usuário para persistência
 */
@Service
@Slf4j
public class AmbienteService {

    private static final String SESSION_AMBIENTE_KEY = "ambiente";
    private static final AmbienteEnum DEFAULT_AMBIENTE = AmbienteEnum.PRODUCAO;

    /**
     * Obtém o ambiente atualmente selecionado na sessão do usuário
     * Se não houver ambiente selecionado, retorna o ambiente padrão (PRODUCAO)
     *
     * @param request HttpServletRequest atual
     * @return AmbienteEnum selecionado
     */
    public AmbienteEnum getAmbienteSelecionado(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        AmbienteEnum ambiente = (AmbienteEnum) session.getAttribute(SESSION_AMBIENTE_KEY);
        
        if (ambiente == null) {
            ambiente = DEFAULT_AMBIENTE;
            session.setAttribute(SESSION_AMBIENTE_KEY, ambiente);
            log.debug("Ambiente não encontrado na sessão, definindo padrão: {}", ambiente);
        }
        
        return ambiente;
    }

    /**
     * Define o ambiente selecionado na sessão do usuário
     *
     * @param request HttpServletRequest atual
     * @param ambiente AmbienteEnum a ser armazenado
     */
    public void setAmbienteSelecionado(HttpServletRequest request, AmbienteEnum ambiente) {
        if (ambiente == null) {
            ambiente = DEFAULT_AMBIENTE;
        }
        
        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_AMBIENTE_KEY, ambiente);
        log.debug("Ambiente selecionado definido na sessão: {}", ambiente);
    }

    /**
     * Obtém o ambiente da requisição (parâmetro ou sessão) ou o ambiente padrão se não existir
     *
     * @param request HttpServletRequest atual
     * @param ambienteParam Ambiente passado como parâmetro na requisição (pode ser null)
     * @return AmbienteEnum a ser usado
     */
    public AmbienteEnum resolveAmbiente(HttpServletRequest request, AmbienteEnum ambienteParam) {
        // Se foi passado um ambiente específico como parâmetro, usa ele e atualiza a sessão
        if (ambienteParam != null) {
            setAmbienteSelecionado(request, ambienteParam);
            return ambienteParam;
        }
        
        // Caso contrário, retorna o ambiente da sessão
        return getAmbienteSelecionado(request);
    }
}
