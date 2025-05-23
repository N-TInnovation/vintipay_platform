package cv.ntinnovation.vintipay.adapter.out.persistence.adapter;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.PreferenciasNotificacaoEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.mapper.PreferenciasNotificacaoMapper;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.PreferenciasNotificacaoRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.PreferenciasNotificacao;
import cv.ntinnovation.vintipay.application.port.out.GerenciaPreferenciasNotificacaoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Adaptador para gerenciamento de preferências de notificação
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PreferenciasNotificacaoAdapter implements GerenciaPreferenciasNotificacaoPort {

    private final PreferenciasNotificacaoRepository preferenciasNotificacaoRepository;
    private final PreferenciasNotificacaoMapper preferenciasNotificacaoMapper;

    @Override
    public PreferenciasNotificacao buscarPorUsuario(Long usuarioId) {
        log.info("Buscando preferências de notificação para usuário ID: {}", usuarioId);
        return preferenciasNotificacaoRepository.findByUsuarioId(usuarioId)
                .map(preferenciasNotificacaoMapper::toDomain)
                .orElseGet(() -> PreferenciasNotificacao.getDefault(usuarioId));
    }

    @Override
    public PreferenciasNotificacao salvar(PreferenciasNotificacao preferenciasNotificacao) {
        log.info("Salvando preferências de notificação para usuário ID: {}", preferenciasNotificacao.getUsuarioId());
        
        // Verificar se já existe preferências para este usuário
        PreferenciasNotificacaoEntity entity;
        if (preferenciasNotificacao.getId() != null) {
            entity = preferenciasNotificacaoMapper.toEntity(preferenciasNotificacao);
        } else {
            // Buscar se já existe antes de criar um novo
            entity = preferenciasNotificacaoRepository.findByUsuarioId(preferenciasNotificacao.getUsuarioId())
                    .orElse(preferenciasNotificacaoMapper.toEntity(preferenciasNotificacao));
            
            // Atualizar os campos da entidade existente
            entity.setEmail(preferenciasNotificacao.isEmail());
            entity.setSms(preferenciasNotificacao.isSms());
            entity.setPush(preferenciasNotificacao.isPush());
            entity.setTransacoes(preferenciasNotificacao.isTransacoes());
            entity.setPromocoes(preferenciasNotificacao.isPromocoes());
            entity.setSeguranca(preferenciasNotificacao.isSeguranca());
        }
        
        return preferenciasNotificacaoMapper.toDomain(preferenciasNotificacaoRepository.save(entity));
    }
}
