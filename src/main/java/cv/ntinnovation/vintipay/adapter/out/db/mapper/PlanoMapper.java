package cv.ntinnovation.vintipay.adapter.out.db.mapper;

import cv.ntinnovation.vintipay.adapter.out.db.entity.PlanoEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.Plano;
import org.springframework.stereotype.Component;

@Component
public class PlanoMapper {

    public Plano toDomain(PlanoEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Plano.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .preco(entity.getPreco())
                .periodoCobranca(mapPeriodoCobranca(entity.getPeriodoCobranca()))
                .diasTrial(entity.getDiasTrial())
                .stripePriceId(entity.getStripePriceId())
                .stripeProductId(entity.getStripeProductId())
                .ativo(entity.isAtivo())
                .build();
    }

    public PlanoEntity toEntity(Plano domain) {
        if (domain == null) {
            return null;
        }
        
        return PlanoEntity.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .descricao(domain.getDescricao())
                .preco(domain.getPreco())
                .periodoCobranca(mapPeriodoCobranca(domain.getPeriodoCobranca()))
                .diasTrial(domain.getDiasTrial())
                .stripePriceId(domain.getStripePriceId())
                .stripeProductId(domain.getStripeProductId())
                .ativo(domain.isAtivo())
                .build();
    }
    
    private Plano.PeriodoCobranca mapPeriodoCobranca(PlanoEntity.PeriodoCobranca periodoCobranca) {
        if (periodoCobranca == null) {
            return null;
        }
        
        return switch (periodoCobranca) {
            case MENSAL -> Plano.PeriodoCobranca.MENSAL;
            case TRIMESTRAL -> Plano.PeriodoCobranca.TRIMESTRAL;
            case SEMESTRAL -> Plano.PeriodoCobranca.SEMESTRAL;
            case ANUAL -> Plano.PeriodoCobranca.ANUAL;
        };
    }
    
    private PlanoEntity.PeriodoCobranca mapPeriodoCobranca(Plano.PeriodoCobranca periodoCobranca) {
        if (periodoCobranca == null) {
            return null;
        }
        
        return switch (periodoCobranca) {
            case MENSAL -> PlanoEntity.PeriodoCobranca.MENSAL;
            case TRIMESTRAL -> PlanoEntity.PeriodoCobranca.TRIMESTRAL;
            case SEMESTRAL -> PlanoEntity.PeriodoCobranca.SEMESTRAL;
            case ANUAL -> PlanoEntity.PeriodoCobranca.ANUAL;
        };
    }
}
