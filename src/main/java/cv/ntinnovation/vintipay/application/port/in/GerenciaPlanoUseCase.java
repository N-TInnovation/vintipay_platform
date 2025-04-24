package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.Plano;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface GerenciaPlanoUseCase {
    List<Plano> listarPlanosAtivos();
    List<Plano> listarTodosPlanos();
    Optional<Plano> buscarPlanoPorId(Long id);
    Plano criarPlano(String nome, String descricao, BigDecimal preco, 
                     Plano.PeriodoCobranca periodoCobranca, Integer diasTrial);
    Plano criarPlano(String nome, String descricao, BigDecimal preco, 
                     Plano.PeriodoCobranca periodoCobranca, Integer diasTrial,
                     String stripePriceId, boolean ativo);
    Plano atualizarPlano(Long id, String nome, String descricao, BigDecimal preco, 
                         Plano.PeriodoCobranca periodoCobranca, Integer diasTrial);
    Plano atualizarPlano(Long id, String nome, String descricao, BigDecimal preco, 
                         Plano.PeriodoCobranca periodoCobranca, Integer diasTrial,
                         String stripePriceId, boolean ativo);
    void ativarPlano(Long id);
    void desativarPlano(Long id);
}
