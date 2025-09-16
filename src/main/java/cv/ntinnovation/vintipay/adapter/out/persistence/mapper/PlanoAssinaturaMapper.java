package cv.ntinnovation.vintipay.adapter.out.persistence.mapper;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.PlanoAssinaturaEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.PlanoAssinatura;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlanoAssinaturaMapper {

    private final UsuarioMapper usuarioMapper;
    private final PlanoMapper planoMapper;

    public PlanoAssinatura toDomain(PlanoAssinaturaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return PlanoAssinatura.builder()
                .id(entity.getId())
                .usuario(entity.getUsuario() != null ? usuarioMapper.toDomain(entity.getUsuario()) : null)
                .plano(entity.getPlano() != null ? planoMapper.toDomain(entity.getPlano()) : null)
                .dataInicio(entity.getDataInicio())
                .dataFim(entity.getDataFim())
                .stripeSubscriptionId(entity.getStripeSubscriptionId())
                .status(mapStatus(entity.getStatus()))
                .ultimoPagamento(entity.getUltimoPagamento())
                .proximoPagamento(entity.getProximoPagamento())
                .valorPago(entity.getValorPago())
                .build();
    }

    public PlanoAssinaturaEntity toEntity(PlanoAssinatura domain) {
        if (domain == null) {
            return null;
        }
        
        return PlanoAssinaturaEntity.builder()
                .id(domain.getId())
                .usuario(domain.getUsuario() != null ? usuarioMapper.toEntity(domain.getUsuario()) : null)
                .plano(domain.getPlano() != null ? planoMapper.toEntity(domain.getPlano()) : null)
                .dataInicio(domain.getDataInicio())
                .dataFim(domain.getDataFim())
                .stripeSubscriptionId(domain.getStripeSubscriptionId())
                .status(mapStatus(domain.getStatus()))
                .ultimoPagamento(domain.getUltimoPagamento())
                .proximoPagamento(domain.getProximoPagamento())
                .valorPago(domain.getValorPago())
                .build();
    }
    
    private PlanoAssinatura.StatusAssinatura mapStatus(PlanoAssinaturaEntity.StatusAssinatura status) {
        if (status == null) {
            return null;
        }
        
        switch (status) {
            case ATIVA:
                return PlanoAssinatura.StatusAssinatura.ATIVA;
            case TRIAL:
                return PlanoAssinatura.StatusAssinatura.TRIAL;
            case CANCELADA:
                return PlanoAssinatura.StatusAssinatura.CANCELADA;
            case PENDENTE:
                return PlanoAssinatura.StatusAssinatura.PENDENTE;
            case EXPIRADA:
                return PlanoAssinatura.StatusAssinatura.EXPIRADA;
            default:
                return null;
        }
    }
    
    private PlanoAssinaturaEntity.StatusAssinatura mapStatus(PlanoAssinatura.StatusAssinatura status) {
        if (status == null) {
            return null;
        }
        
        switch (status) {
            case ATIVA:
                return PlanoAssinaturaEntity.StatusAssinatura.ATIVA;
            case TRIAL:
                return PlanoAssinaturaEntity.StatusAssinatura.TRIAL;
            case CANCELADA:
                return PlanoAssinaturaEntity.StatusAssinatura.CANCELADA;
            case PENDENTE:
                return PlanoAssinaturaEntity.StatusAssinatura.PENDENTE;
            case EXPIRADA:
                return PlanoAssinaturaEntity.StatusAssinatura.EXPIRADA;
            default:
                return null;
        }
    }
}
