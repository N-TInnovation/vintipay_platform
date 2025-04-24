package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.Plano;
import cv.ntinnovation.vintipay.application.core.domain.entity.PlanoAssinatura;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.GerenciaAssinaturaUseCase;
import cv.ntinnovation.vintipay.application.port.in.StripeIntegrationUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaPlanoAssinaturaPort;
import cv.ntinnovation.vintipay.application.port.out.BuscaPlanoPort;
import cv.ntinnovation.vintipay.application.port.out.SalvaPlanoAssinaturaPort;
import cv.ntinnovation.vintipay.application.port.out.SalvaUsuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GerenciaAssinaturaUseCaseImpl implements GerenciaAssinaturaUseCase {

    private final BuscaPlanoPort buscaPlanoPort;
    private final BuscaPlanoAssinaturaPort buscaPlanoAssinaturaPort;
    private final SalvaPlanoAssinaturaPort salvaPlanoAssinaturaPort;
    private final SalvaUsuario salvaUsuario;
    private final StripeIntegrationUseCase stripeIntegrationUseCase;

    @Override
    @Transactional
    public PlanoAssinatura criarAssinatura(Usuario usuario, Long planoId) {
        return criarAssinatura(usuario, planoId, false);
    }

    @Override
    @Transactional
    public PlanoAssinatura criarAssinatura(Usuario usuario, Long planoId, boolean skipStripeIntegration) {
        // Buscar o plano
        Optional<Plano> planoOptional = buscaPlanoPort.buscarPorId(planoId);
        if (planoOptional.isEmpty()) {
            throw new RuntimeException("Plano não encontrado com o ID: " + planoId);
        }
        
        Plano plano = planoOptional.get();
        
        // Verificar se o usuário já tem uma assinatura
        Optional<PlanoAssinatura> assinaturaExistente = buscaPlanoAssinaturaPort.buscarPorUsuario(usuario);
        if (assinaturaExistente.isPresent()) {
            throw new RuntimeException("Usuário já possui uma assinatura ativa");
        }
        
        String stripeSubscriptionId = null;
        
        // Se não estamos pulando a integração com o Stripe
        if (!skipStripeIntegration) {
            // Criar customer no Stripe se ainda não existir
            if (usuario.getStripeCustomerId() == null || usuario.getStripeCustomerId().isEmpty()) {
                String stripeCustomerId = stripeIntegrationUseCase.criarCustomer(usuario);
                usuario.setStripeCustomerId(stripeCustomerId);
                salvaUsuario.salvar(usuario);
            }
            
            // Criar assinatura no Stripe
            stripeSubscriptionId = stripeIntegrationUseCase.criarSubscription(usuario, plano.getStripePriceId());
        }
        
        // Calcular datas de início e fim
        LocalDateTime dataInicio = LocalDateTime.now();
        LocalDateTime dataFim = null;
        
        // Se tem período trial, definir status como TRIAL
        PlanoAssinatura.StatusAssinatura status = PlanoAssinatura.StatusAssinatura.ATIVA;
        if (plano.getDiasTrial() != null && plano.getDiasTrial() > 0) {
            status = PlanoAssinatura.StatusAssinatura.TRIAL;
        }
        
        // Criar assinatura no banco de dados
        PlanoAssinatura assinatura = PlanoAssinatura.builder()
                .usuario(usuario)
                .plano(plano)
                .dataInicio(dataInicio)
                .dataFim(dataFim)
                .stripeSubscriptionId(stripeSubscriptionId)
                .status(status)
                .ultimoPagamento(dataInicio)
                .build();
        
        return salvaPlanoAssinaturaPort.salvar(assinatura);
    }
    
    @Override
    @Transactional
    public boolean criarNovaAssinatura(Usuario usuario, Long planoId) {
        try {
            log.info("Criando nova assinatura para usuário ID: {} com plano ID: {}", usuario.getId(), planoId);
            
            // Verificar se o plano existe
            Optional<Plano> planoOptional = buscaPlanoPort.buscarPorId(planoId);
            if (planoOptional.isEmpty()) {
                log.error("Plano não encontrado com o ID: {}", planoId);
                return false;
            }
            
            // Verificar se o usuário já tem uma assinatura
            Optional<PlanoAssinatura> assinaturaExistente = buscaPlanoAssinaturaPort.buscarPorUsuario(usuario);
            if (assinaturaExistente.isPresent()) {
                log.warn("Usuário já possui uma assinatura ativa");
                return false;
            }
            
            // Criar a assinatura
            criarAssinatura(usuario, planoId);
            log.info("Nova assinatura criada com sucesso para usuário ID: {}", usuario.getId());
            return true;
        } catch (Exception e) {
            log.error("Erro ao criar nova assinatura: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Optional<PlanoAssinatura> buscarAssinaturaPorUsuario(Usuario usuario) {
        return buscaPlanoAssinaturaPort.buscarPorUsuario(usuario);
    }
    
    @Override
    public Optional<PlanoAssinatura> buscarAssinaturaPorId(Long id) {
        log.info("Buscando assinatura por ID: {}", id);
        Optional<PlanoAssinatura> assinaturaOptional = buscaPlanoAssinaturaPort.buscarPorId(id);
        if (assinaturaOptional.isPresent()) {
            log.info("Assinatura encontrada: {}", assinaturaOptional.get().getId());
        } else {
            log.info("Assinatura não encontrada para o ID: {}", id);
        }
        return assinaturaOptional;
    }

    @Override
    @Transactional
    public void cancelarAssinatura(Usuario usuario) {
        Optional<PlanoAssinatura> assinaturaOptional = buscaPlanoAssinaturaPort.buscarPorUsuario(usuario);
        if (assinaturaOptional.isEmpty()) {
            throw new RuntimeException("Usuário não possui assinatura ativa");
        }
        
        PlanoAssinatura assinatura = assinaturaOptional.get();
        
        // Se tiver ID do Stripe, cancelar no Stripe também
        if (assinatura.getStripeSubscriptionId() != null && !assinatura.getStripeSubscriptionId().isEmpty()) {
            stripeIntegrationUseCase.cancelarSubscription(assinatura.getStripeSubscriptionId());
        }
        
        // Atualizar status da assinatura
        assinatura.setStatus(PlanoAssinatura.StatusAssinatura.CANCELADA);
        assinatura.setDataFim(LocalDateTime.now());
        
        salvaPlanoAssinaturaPort.salvar(assinatura);
    }
    
    @Override
    @Transactional
    public boolean cancelarAssinatura(Long assinaturaId) {
        try {
            log.info("Cancelando assinatura ID: {}", assinaturaId);
            
            // Buscar a assinatura
            Optional<PlanoAssinatura> assinaturaOptional = buscaPlanoAssinaturaPort.buscarPorId(assinaturaId);
            if (assinaturaOptional.isEmpty()) {
                log.warn("Assinatura não encontrada para o ID: {}", assinaturaId);
                return false;
            }
            
            PlanoAssinatura assinatura = assinaturaOptional.get();
            
            // Se tiver ID do Stripe, cancelar no Stripe também
            if (assinatura.getStripeSubscriptionId() != null && !assinatura.getStripeSubscriptionId().isEmpty()) {
                stripeIntegrationUseCase.cancelarSubscription(assinatura.getStripeSubscriptionId());
            }
            
            // Atualizar status da assinatura
            assinatura.setStatus(PlanoAssinatura.StatusAssinatura.CANCELADA);
            assinatura.setDataFim(LocalDateTime.now());
            
            salvaPlanoAssinaturaPort.salvar(assinatura);
            log.info("Assinatura cancelada com sucesso: {}", assinaturaId);
            return true;
        } catch (Exception e) {
            log.error("Erro ao cancelar assinatura: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public void renovarAssinatura(String stripeSubscriptionId) {
        Optional<PlanoAssinatura> assinaturaOptional = buscaPlanoAssinaturaPort.buscarPorStripeSubscriptionId(stripeSubscriptionId);
        if (assinaturaOptional.isEmpty()) {
            log.error("Assinatura não encontrada para o Stripe Subscription ID: {}", stripeSubscriptionId);
            return;
        }
        
        PlanoAssinatura assinatura = assinaturaOptional.get();
        
        // Atualizar data do último pagamento
        assinatura.setUltimoPagamento(LocalDateTime.now());
        
        // Se estiver em trial, mudar para ativa
        if (assinatura.getStatus() == PlanoAssinatura.StatusAssinatura.TRIAL) {
            assinatura.setStatus(PlanoAssinatura.StatusAssinatura.ATIVA);
        }
        
        // Calcular próxima data de pagamento (30 dias a partir de hoje)
        LocalDateTime proximoPagamento = LocalDateTime.now().plusDays(30);
        assinatura.setProximoPagamento(proximoPagamento);
        
        salvaPlanoAssinaturaPort.salvar(assinatura);
        
        log.info("Assinatura renovada com sucesso: {}", stripeSubscriptionId);
    }

    @Override
    @Transactional
    public void atualizarStatusAssinatura(String stripeSubscriptionId, PlanoAssinatura.StatusAssinatura novoStatus) {
        Optional<PlanoAssinatura> assinaturaOptional = buscaPlanoAssinaturaPort.buscarPorStripeSubscriptionId(stripeSubscriptionId);
        if (assinaturaOptional.isEmpty()) {
            log.error("Assinatura não encontrada para o Stripe Subscription ID: {}", stripeSubscriptionId);
            return;
        }
        
        PlanoAssinatura assinatura = assinaturaOptional.get();
        
        // Atualizar status
        assinatura.setStatus(novoStatus);
        
        // Se for cancelada, definir data de fim
        if (novoStatus == PlanoAssinatura.StatusAssinatura.CANCELADA) {
            assinatura.setDataFim(LocalDateTime.now());
        }
        
        salvaPlanoAssinaturaPort.salvar(assinatura);
        
        log.info("Status da assinatura atualizado para {}: {}", novoStatus, stripeSubscriptionId);
    }
    
    @Override
    @Transactional
    public boolean atualizarPlanoAssinatura(Long assinaturaId, Long novoPlanoId) {
        try {
            log.info("Atualizando assinatura ID: {} para o plano ID: {}", assinaturaId, novoPlanoId);
            
            // Buscar a assinatura
            Optional<PlanoAssinatura> assinaturaOptional = buscaPlanoAssinaturaPort.buscarPorId(assinaturaId);
            if (assinaturaOptional.isEmpty()) {
                log.warn("Assinatura não encontrada para o ID: {}", assinaturaId);
                return false;
            }
            
            // Buscar o novo plano
            Optional<Plano> planoOptional = buscaPlanoPort.buscarPorId(novoPlanoId);
            if (planoOptional.isEmpty()) {
                log.warn("Plano não encontrado com o ID: {}", novoPlanoId);
                return false;
            }
            
            PlanoAssinatura assinatura = assinaturaOptional.get();
            Plano novoPlano = planoOptional.get();
            
            // Verificar se não é o mesmo plano
            if (assinatura.getPlano().getId().equals(novoPlanoId)) {
                log.warn("A assinatura já está no plano solicitado: {}", novoPlanoId);
                return false;
            }
            
            // Verificar se o novo plano tem um stripePriceId válido
            if (novoPlano.getStripePriceId() == null || novoPlano.getStripePriceId().isEmpty()) {
                log.error("O novo plano não possui um ID de preço do Stripe válido: {}", novoPlanoId);
                return false;
            }
            
            // Se tiver ID do Stripe, atualizar no Stripe também
            if (assinatura.getStripeSubscriptionId() != null && !assinatura.getStripeSubscriptionId().isEmpty()) {
                try {
                    log.info("Atualizando assinatura no Stripe: {} para o preço: {}", 
                            assinatura.getStripeSubscriptionId(), novoPlano.getStripePriceId());
                    
                    String updatedSubscriptionId = stripeIntegrationUseCase.atualizarSubscription(
                            assinatura.getStripeSubscriptionId(), 
                            novoPlano.getStripePriceId());
                    
                    log.info("Assinatura atualizada no Stripe com sucesso: {}", updatedSubscriptionId);
                    
                    // Se o ID da assinatura mudou, atualizar no banco de dados
                    if (!updatedSubscriptionId.equals(assinatura.getStripeSubscriptionId())) {
                        assinatura.setStripeSubscriptionId(updatedSubscriptionId);
                    }
                } catch (Exception e) {
                    log.error("Erro ao atualizar assinatura no Stripe: {}", e.getMessage(), e);
                    // Continuar com a atualização no banco de dados mesmo se falhar no Stripe
                }
            } else {
                log.warn("Assinatura não possui ID do Stripe, atualizando apenas no banco de dados");
            }
            
            // Atualizar o plano da assinatura
            assinatura.setPlano(novoPlano);
            
            // Se a assinatura estiver cancelada, reativar
            if (assinatura.getStatus() == PlanoAssinatura.StatusAssinatura.CANCELADA) {
                assinatura.setStatus(PlanoAssinatura.StatusAssinatura.ATIVA);
                assinatura.setDataFim(null);
            }
            
            // Salvar as alterações
            salvaPlanoAssinaturaPort.salvar(assinatura);
            log.info("Assinatura atualizada com sucesso para o plano: {}", novoPlano.getNome());
            return true;
        } catch (Exception e) {
            log.error("Erro ao atualizar plano da assinatura: {}", e.getMessage(), e);
            return false;
        }
    }
}
