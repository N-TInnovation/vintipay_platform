package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.Plano;
import cv.ntinnovation.vintipay.application.port.in.GerenciaPlanoUseCase;
import cv.ntinnovation.vintipay.application.port.in.StripeIntegrationUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaPlanoPort;
import cv.ntinnovation.vintipay.application.port.out.SalvaPlanoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GerenciaPlanoUseCaseImpl implements GerenciaPlanoUseCase {

    private final BuscaPlanoPort buscaPlanoPort;
    private final SalvaPlanoPort salvaPlanoPort;
    private final StripeIntegrationUseCase stripeIntegrationUseCase;

    @Override
    public List<Plano> listarPlanosAtivos() {
        return buscaPlanoPort.buscarTodosAtivos();
    }

    @Override
    public Optional<Plano> buscarPlanoPorId(Long id) {
        return buscaPlanoPort.buscarPorId(id);
    }

    @Override
    @Transactional
    public Plano criarPlano(String nome, String descricao, BigDecimal preco, 
                           Plano.PeriodoCobranca periodoCobranca, Integer diasTrial) {
        // Criar o produto no Stripe
        String stripeProductId = stripeIntegrationUseCase.criarProduto(nome, descricao);
        
        // Criar o preço no Stripe
        long valorCentavos = preco.multiply(new BigDecimal("100")).longValue();
        String intervalo = mapPeriodoCobrancaParaStripe(periodoCobranca);
        String stripePriceId = stripeIntegrationUseCase.criarPreco(stripeProductId, valorCentavos, "brl", intervalo);
        
        // Criar o plano no banco de dados
        Plano plano = Plano.builder()
                .nome(nome)
                .descricao(descricao)
                .preco(preco)
                .periodoCobranca(periodoCobranca)
                .diasTrial(diasTrial)
                .stripeProductId(stripeProductId)
                .stripePriceId(stripePriceId)
                .ativo(true)
                .build();
        
        return salvaPlanoPort.salvar(plano);
    }

    @Override
    @Transactional
    public Plano atualizarPlano(Long id, String nome, String descricao, BigDecimal preco, 
                               Plano.PeriodoCobranca periodoCobranca, Integer diasTrial) {
        Optional<Plano> planoOptional = buscaPlanoPort.buscarPorId(id);
        if (planoOptional.isEmpty()) {
            throw new RuntimeException("Plano não encontrado com o ID: " + id);
        }
        
        Plano plano = planoOptional.get();
        plano.setNome(nome);
        plano.setDescricao(descricao);
        plano.setPreco(preco);
        plano.setPeriodoCobranca(periodoCobranca);
        plano.setDiasTrial(diasTrial);
        
        // Atualizar no Stripe não está implementado neste exemplo
        // Seria necessário implementar a atualização do produto e preço no Stripe
        
        return salvaPlanoPort.salvar(plano);
    }

    @Override
    @Transactional
    public void ativarPlano(Long id) {
        Optional<Plano> planoOptional = buscaPlanoPort.buscarPorId(id);
        if (planoOptional.isEmpty()) {
            throw new RuntimeException("Plano não encontrado com o ID: " + id);
        }
        
        Plano plano = planoOptional.get();
        plano.setAtivo(true);
        salvaPlanoPort.salvar(plano);
    }

    @Override
    @Transactional
    public void desativarPlano(Long id) {
        Optional<Plano> planoOptional = buscaPlanoPort.buscarPorId(id);
        if (planoOptional.isEmpty()) {
            throw new RuntimeException("Plano não encontrado com o ID: " + id);
        }
        
        Plano plano = planoOptional.get();
        plano.setAtivo(false);
        salvaPlanoPort.salvar(plano);
    }
    
    private String mapPeriodoCobrancaParaStripe(Plano.PeriodoCobranca periodoCobranca) {
        return switch (periodoCobranca) {
            case MENSAL -> "month";
            case TRIMESTRAL -> "month"; // Stripe não tem trimestral, seria necessário ajustar
            case SEMESTRAL -> "month"; // Stripe não tem semestral, seria necessário ajustar
            case ANUAL -> "year";
        };
    }
}
