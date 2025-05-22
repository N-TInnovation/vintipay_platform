package cv.ntinnovation.vintipay.adapter.out.db.mapper;

import cv.ntinnovation.vintipay.adapter.out.db.entity.PlanoAssinaturaEntity;
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
        
        return switch (status) {
            case ATIVA -> PlanoAssinatura.StatusAssinatura.ATIVA;
            case TRIAL -> PlanoAssinatura.StatusAssinatura.TRIAL;
            case CANCELADA -> PlanoAssinatura.StatusAssinatura.CANCELADA;
            case PENDENTE -> PlanoAssinatura.StatusAssinatura.PENDENTE;
            case EXPIRADA -> PlanoAssinatura.StatusAssinatura.EXPIRADA;
        };
    }
    
    private PlanoAssinaturaEntity.StatusAssinatura mapStatus(PlanoAssinatura.StatusAssinatura status) {
        if (status == null) {
            return null;
        }
        
        return switch (status) {
            case ATIVA -> PlanoAssinaturaEntity.StatusAssinatura.ATIVA;
            case TRIAL -> PlanoAssinaturaEntity.StatusAssinatura.TRIAL;
            case CANCELADA -> PlanoAssinaturaEntity.StatusAssinatura.CANCELADA;
            case PENDENTE -> PlanoAssinaturaEntity.StatusAssinatura.PENDENTE;
            case EXPIRADA -> PlanoAssinaturaEntity.StatusAssinatura.EXPIRADA;
        };
    }
}
