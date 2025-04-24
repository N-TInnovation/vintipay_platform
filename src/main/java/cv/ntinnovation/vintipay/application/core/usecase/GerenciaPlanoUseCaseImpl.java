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
    public List<Plano> listarTodosPlanos() {
        return buscaPlanoPort.buscarTodos();
    }

    @Override
    public Optional<Plano> buscarPlanoPorId(Long id) {
        return buscaPlanoPort.buscarPorId(id);
    }

    @Override
    @Transactional
    public Plano criarPlano(String nome, String descricao, BigDecimal preco, 
                           Plano.PeriodoCobranca periodoCobranca, Integer diasTrial) {
        return criarPlano(nome, descricao, preco, periodoCobranca, diasTrial, null, true);
    }

    @Override
    @Transactional
    public Plano criarPlano(String nome, String descricao, BigDecimal preco, 
                           Plano.PeriodoCobranca periodoCobranca, Integer diasTrial,
                           String stripePriceId, boolean ativo) {
        try {
            log.debug("Criando plano: {}, preço: {}, período: {}", nome, preco, periodoCobranca);
            
            // Verificar se já existe um plano com o mesmo nome
            Optional<Plano> planoExistente = buscaPlanoPort.buscarPorNome(nome);
            if (planoExistente.isPresent()) {
                throw new IllegalArgumentException("Já existe um plano com o nome '" + nome + "'. Por favor, escolha um nome diferente.");
            }
            
            String stripeProductId = null;
            String stripePriceIdFinal = stripePriceId;
            
            // Se não foi fornecido um stripePriceId, criar no Stripe
            if (stripePriceIdFinal == null || stripePriceIdFinal.isEmpty()) {
                // Criar o produto no Stripe
                stripeProductId = stripeIntegrationUseCase.criarProduto(nome, descricao);
                log.debug("Produto criado no Stripe: {}", stripeProductId);
                
                // Criar o preço no Stripe
                long valorCentavos = preco.multiply(new BigDecimal("100")).longValue();
                String intervalo = mapPeriodoCobrancaParaStripe(periodoCobranca);
                int intervalCount = getIntervalCount(periodoCobranca);
                
                log.debug("Criando preço no Stripe: valor={}, moeda=brl, intervalo={}, contagem={}", 
                         valorCentavos, intervalo, intervalCount);
                
                stripePriceIdFinal = stripeIntegrationUseCase.criarPreco(
                    stripeProductId, 
                    valorCentavos, 
                    "brl", 
                    intervalo,
                    intervalCount
                );
                log.debug("Preço criado no Stripe: {}", stripePriceIdFinal);
            }
            
            // Criar o plano no banco de dados
            Plano plano = Plano.builder()
                    .nome(nome)
                    .descricao(descricao)
                    .preco(preco)
                    .periodoCobranca(periodoCobranca)
                    .diasTrial(diasTrial)
                    .stripeProductId(stripeProductId)
                    .stripePriceId(stripePriceIdFinal)
                    .ativo(ativo)
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
        return atualizarPlano(id, nome, descricao, preco, periodoCobranca, diasTrial, null, true);
    }

    @Override
    @Transactional
    public Plano atualizarPlano(Long id, String nome, String descricao, BigDecimal preco, 
                               Plano.PeriodoCobranca periodoCobranca, Integer diasTrial,
                               String stripePriceId, boolean ativo) {
        try {
            log.debug("Atualizando plano: {}, nome: {}, preço: {}, período: {}", id, nome, preco, periodoCobranca);
            
            // Verificar se o plano existe
            Optional<Plano> planoOptional = buscaPlanoPort.buscarPorId(id);
            if (planoOptional.isEmpty()) {
                throw new IllegalArgumentException("Plano não encontrado com ID: " + id);
            }
            
            Plano planoExistente = planoOptional.get();
            
            // Verificar se já existe outro plano com o mesmo nome (exceto o próprio plano sendo atualizado)
            if (!planoExistente.getNome().equals(nome)) {
                Optional<Plano> outroPlanoComMesmoNome = buscaPlanoPort.buscarPorNome(nome);
                if (outroPlanoComMesmoNome.isPresent() && !outroPlanoComMesmoNome.get().getId().equals(id)) {
                    throw new IllegalArgumentException("Já existe um plano com o nome '" + nome + "'. Por favor, escolha um nome diferente.");
                }
            }
            
            // Atualizar os dados do plano
            planoExistente.setNome(nome);
            planoExistente.setDescricao(descricao);
            planoExistente.setPreco(preco);
            planoExistente.setPeriodoCobranca(periodoCobranca);
            planoExistente.setDiasTrial(diasTrial);
            planoExistente.setStripePriceId(stripePriceId);
            planoExistente.setAtivo(ativo);
            
            // Salvar as alterações
            Plano planoAtualizado = salvaPlanoPort.salvar(planoExistente);
            log.info("Plano atualizado com sucesso: {}", planoAtualizado.getId());
            return planoAtualizado;
        } catch (Exception e) {
            log.error("Erro ao atualizar plano: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao atualizar plano: " + e.getMessage(), e);
        }
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
