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
        try {
            log.debug("Criando plano: {}, preço: {}, período: {}", nome, preco, periodoCobranca);
            
            // Criar o produto no Stripe
            String stripeProductId = stripeIntegrationUseCase.criarProduto(nome, descricao);
            log.debug("Produto criado no Stripe: {}", stripeProductId);
            
            // Criar o preço no Stripe
            long valorCentavos = preco.multiply(new BigDecimal("100")).longValue();
            String intervalo = mapPeriodoCobrancaParaStripe(periodoCobranca);
            int intervalCount = getIntervalCount(periodoCobranca);
            
            log.debug("Criando preço no Stripe: valor={}, moeda=brl, intervalo={}, contagem={}", 
                     valorCentavos, intervalo, intervalCount);
            
            String stripePriceId = stripeIntegrationUseCase.criarPreco(
                stripeProductId, 
                valorCentavos, 
                "brl", 
                intervalo,
                intervalCount
            );
            log.debug("Preço criado no Stripe: {}", stripePriceId);
            
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
            
            Plano planoSalvo = salvaPlanoPort.salvar(plano);
            log.info("Plano criado com sucesso: {}", planoSalvo.getId());
            return planoSalvo;
        } catch (Exception e) {
            log.error("Erro ao criar plano: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao criar plano: " + e.getMessage(), e);
        }
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
        
        // Atualizar no Stripe
        try {
            // Atualizar produto no Stripe
            stripeIntegrationUseCase.atualizarProduto(plano.getStripeProductId(), nome, descricao);
            
            // Para atualizar o preço, precisamos criar um novo preço no Stripe
            // já que o Stripe não permite atualizar preços existentes
            long valorCentavos = preco.multiply(new BigDecimal("100")).longValue();
            String intervalo = mapPeriodoCobrancaParaStripe(periodoCobranca);
            int intervalCount = getIntervalCount(periodoCobranca);
            
            String novoStripePriceId = stripeIntegrationUseCase.criarPreco(
                plano.getStripeProductId(), 
                valorCentavos, 
                "brl", 
                intervalo,
                intervalCount
            );
            
            // Atualizar o ID do preço no plano
            plano.setStripePriceId(novoStripePriceId);
            
            log.info("Plano atualizado com sucesso: {}", id);
        } catch (Exception e) {
            log.error("Erro ao atualizar plano no Stripe: {}", e.getMessage(), e);
            // Continuar com a atualização no banco de dados mesmo se falhar no Stripe
        }
        
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
        log.info("Plano ativado: {}", id);
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
        log.info("Plano desativado: {}", id);
    }
    
    private String mapPeriodoCobrancaParaStripe(Plano.PeriodoCobranca periodoCobranca) {
        return switch (periodoCobranca) {
            case MENSAL, TRIMESTRAL, SEMESTRAL -> "month";
            case ANUAL -> "year";
        };
    }
    
    private int getIntervalCount(Plano.PeriodoCobranca periodoCobranca) {
        return switch (periodoCobranca) {
            case MENSAL -> 1;
            case TRIMESTRAL -> 3;
            case SEMESTRAL -> 6;
            case ANUAL -> 1;
        };
    }
}
