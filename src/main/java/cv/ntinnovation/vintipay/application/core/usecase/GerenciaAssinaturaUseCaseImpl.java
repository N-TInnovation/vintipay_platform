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
        
        // Criar customer no Stripe se ainda não existir
        if (usuario.getStripeCustomerId() == null || usuario.getStripeCustomerId().isEmpty()) {
            String stripeCustomerId = stripeIntegrationUseCase.criarCustomer(usuario);
            usuario.setStripeCustomerId(stripeCustomerId);
            salvaUsuario.salvar(usuario);
        }
        
        // Criar assinatura no Stripe
        String stripeSubscriptionId = stripeIntegrationUseCase.criarSubscription(usuario, plano.getStripePriceId());
        
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
    public Optional<PlanoAssinatura> buscarAssinaturaPorUsuario(Usuario usuario) {
        return buscaPlanoAssinaturaPort.buscarPorUsuario(usuario);
    }

    @Override
    @Transactional
    public void cancelarAssinatura(Usuario usuario) {
        Optional<PlanoAssinatura> assinaturaOptional = buscaPlanoAssinaturaPort.buscarPorUsuario(usuario);
        if (assinaturaOptional.isEmpty()) {
            throw new RuntimeException("Usuário não possui assinatura ativa");
        }
        
        PlanoAssinatura assinatura = assinaturaOptional.get();
        
        // Cancelar assinatura no Stripe
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
    public void renovarAssinatura(String stripeSubscriptionId) {
        Optional<PlanoAssinatura> assinaturaOptional = buscaPlanoAssinaturaPort.buscarPorStripeSubscriptionId(stripeSubscriptionId);
        if (assinaturaOptional.isEmpty()) {
            throw new RuntimeException("Assinatura não encontrada com o ID: " + stripeSubscriptionId);
        }
        
        PlanoAssinatura assinatura = assinaturaOptional.get();
        
        // Atualizar datas e status
        assinatura.setUltimoPagamento(LocalDateTime.now());
        assinatura.setStatus(PlanoAssinatura.StatusAssinatura.ATIVA);
        assinatura.setDataFim(null); // Assinatura ativa não tem data fim
        
        salvaPlanoAssinaturaPort.salvar(assinatura);
    }

    @Override
    @Transactional
    public void atualizarStatusAssinatura(String stripeSubscriptionId, PlanoAssinatura.StatusAssinatura novoStatus) {
        Optional<PlanoAssinatura> assinaturaOptional = buscaPlanoAssinaturaPort.buscarPorStripeSubscriptionId(stripeSubscriptionId);
        if (assinaturaOptional.isEmpty()) {
            throw new RuntimeException("Assinatura não encontrada com o ID: " + stripeSubscriptionId);
        }
        
        PlanoAssinatura assinatura = assinaturaOptional.get();
        
        // Atualizar status
        assinatura.setStatus(novoStatus);
        
        // Se for cancelada ou expirada, definir data fim
        if (novoStatus == PlanoAssinatura.StatusAssinatura.CANCELADA || 
            novoStatus == PlanoAssinatura.StatusAssinatura.EXPIRADA) {
            assinatura.setDataFim(LocalDateTime.now());
        }
        
        salvaPlanoAssinaturaPort.salvar(assinatura);
    }
}
