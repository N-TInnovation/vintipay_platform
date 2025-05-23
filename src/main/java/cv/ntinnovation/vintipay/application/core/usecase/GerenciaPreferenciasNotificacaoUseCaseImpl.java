package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.PreferenciasNotificacao;
import cv.ntinnovation.vintipay.application.port.in.GerenciaPreferenciasNotificacaoUseCase;
import cv.ntinnovation.vintipay.application.port.out.GerenciaPreferenciasNotificacaoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementação do caso de uso para gerenciamento de preferências de notificação
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GerenciaPreferenciasNotificacaoUseCaseImpl implements GerenciaPreferenciasNotificacaoUseCase {

    private final GerenciaPreferenciasNotificacaoPort gerenciaPreferenciasNotificacaoPort;
    
    @Override
    @Transactional(readOnly = true)
    public PreferenciasNotificacao buscarPreferenciasDoUsuario(Long usuarioId) {
        log.info("Buscando preferências de notificação para o usuário ID: {}", usuarioId);
        return gerenciaPreferenciasNotificacaoPort.buscarPorUsuario(usuarioId);
    }
    
    @Override
    @Transactional
    public PreferenciasNotificacao atualizarPreferencias(PreferenciasNotificacao preferenciasNotificacao) {
        log.info("Atualizando preferências de notificação para o usuário ID: {}", preferenciasNotificacao.getUsuarioId());
        return gerenciaPreferenciasNotificacaoPort.salvar(preferenciasNotificacao);
    }
}
