package cv.ntinnovation.vintipay.adapter.out.persistence.mapper;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.PreferenciasNotificacaoEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.PreferenciasNotificacao;
import org.springframework.stereotype.Component;

/**
 * Mapeador para converter entre entidade de domínio e entidade JPA das preferências de notificação
 */
@Component
public class PreferenciasNotificacaoMapper {

    /**
     * Converte de entidade JPA para entidade de domínio
     * 
     * @param entity Entidade JPA
     * @return Entidade de domínio
     */
    public PreferenciasNotificacao toDomain(PreferenciasNotificacaoEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return PreferenciasNotificacao.builder()
                .id(entity.getId())
                .usuarioId(entity.getUsuarioId())
                .email(entity.isEmail())
                .sms(entity.isSms())
                .push(entity.isPush())
                .transacoes(entity.isTransacoes())
                .promocoes(entity.isPromocoes())
                .seguranca(entity.isSeguranca())
                .build();
    }
    
    /**
     * Converte de entidade de domínio para entidade JPA
     * 
     * @param domain Entidade de domínio
     * @return Entidade JPA
     */
    public PreferenciasNotificacaoEntity toEntity(PreferenciasNotificacao domain) {
        if (domain == null) {
            return null;
        }
        
        return PreferenciasNotificacaoEntity.builder()
                .id(domain.getId())
                .usuarioId(domain.getUsuarioId())
                .email(domain.isEmail())
                .sms(domain.isSms())
                .push(domain.isPush())
                .transacoes(domain.isTransacoes())
                .promocoes(domain.isPromocoes())
                .seguranca(domain.isSeguranca())
                .build();
    }
}
