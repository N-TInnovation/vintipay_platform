package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.security.CustomOAuth2User;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.PlanoAssinatura;
import cv.ntinnovation.vintipay.application.core.domain.entity.Plano;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.BuscaAssinaturaDirectaUseCase;
import cv.ntinnovation.vintipay.application.port.in.BuscaHistoricoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.GerenciaAssinaturaUseCase;
import cv.ntinnovation.vintipay.application.port.in.GerenciaPlanoUseCase;
import cv.ntinnovation.vintipay.application.port.in.StripeIntegrationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Controller
@RequestMapping("/user/assinatura")
@Log
public class UserAssinaturaController {

    private final BuscaHistoricoPagamentoUseCase buscaHistoricoPagamentoUseCase;
    private final GerenciaAssinaturaUseCase gerenciaAssinaturaUseCase;
    private final GerenciaPlanoUseCase gerenciaPlanoUseCase;
    private final BuscaAssinaturaDirectaUseCase buscaAssinaturaDirectaUseCase;
    private final StripeIntegrationUseCase stripeIntegrationUseCase;
    
    @Value("${app.base-url:https://localhost:8443}")
    private String baseUrl;

    @Autowired
    public UserAssinaturaController(
            BuscaHistoricoPagamentoUseCase buscaHistoricoPagamentoUseCase,
            GerenciaAssinaturaUseCase gerenciaAssinaturaUseCase,
            GerenciaPlanoUseCase gerenciaPlanoUseCase,
            BuscaAssinaturaDirectaUseCase buscaAssinaturaDirectaUseCase,
            StripeIntegrationUseCase stripeIntegrationUseCase) {
        this.buscaHistoricoPagamentoUseCase = buscaHistoricoPagamentoUseCase;
        this.gerenciaAssinaturaUseCase = gerenciaAssinaturaUseCase;
        this.gerenciaPlanoUseCase = gerenciaPlanoUseCase;
        this.buscaAssinaturaDirectaUseCase = buscaAssinaturaDirectaUseCase;
        this.stripeIntegrationUseCase = stripeIntegrationUseCase;
        log.info("UserAssinaturaController inicializado com sucesso");
    }

    @GetMapping({"", "/"})
    public String assinaturaPage(Model model, 
                                @RequestParam(required = false, defaultValue = "0") int page,
                                @RequestParam(required = false, defaultValue = "10") int size,
                                @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        try {
            log.info("Iniciando carregamento da página de assinatura");
            
            // Obter o usuário autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuarioAtual = customOAuth2User.getUsuario();
            log.info("Usuário autenticado: " + usuarioAtual.getEmail());
            
            // Adicionar dados do usuário ao modelo
            model.addAttribute("usuario", usuarioAtual);
            
            // Buscar assinatura do usuário usando o caso de uso dedicado
            Optional<PlanoAssinatura> assinaturaDirectaOptional = buscaAssinaturaDirectaUseCase.buscarAssinaturaPorUsuarioId(usuarioAtual.getId());
            
            if (assinaturaDirectaOptional.isPresent()) {
                log.info("Assinatura encontrada pelo caso de uso direto");
                PlanoAssinatura assinatura = assinaturaDirectaOptional.get();
                
                // Buscar a assinatura completa usando o caso de uso
                Optional<PlanoAssinatura> assinaturaDetalhadaOptional = gerenciaAssinaturaUseCase.buscarAssinaturaPorId(assinatura.getId());
                
                if (assinaturaDetalhadaOptional.isPresent()) {
                    log.info("Detalhes da assinatura encontrados pelo caso de uso");
                    assinatura = assinaturaDetalhadaOptional.get();
                    Plano plano = assinatura.getPlano();
                    
                    // Formatar datas
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String dataInicio = assinatura.getDataInicio().format(formatter);
                    String dataRenovacao = assinatura.getProximoPagamento() != null ? 
                                          assinatura.getProximoPagamento().format(formatter) : 
                                          "Não disponível";
                    
                    // Adicionar informações da assinatura ao modelo
                    model.addAttribute("planoAtual", plano.getNome());
                    model.addAttribute("dataInicio", dataInicio);
                    model.addAttribute("dataRenovacao", dataRenovacao);
                    model.addAttribute("statusAssinatura", assinatura.getStatus().toString());
                    model.addAttribute("valorMensal", "ECV " + plano.getPreco());
                    model.addAttribute("assinaturaId", assinatura.getId());
                    model.addAttribute("planoId", plano.getId());
                    
                    // Adicionar recursos do plano
                    String[] recursos = plano.getDescricao().split(",");
                    model.addAttribute("recursos", recursos);
                    
                    log.info("Dados da assinatura adicionados ao modelo: " + plano.getNome() + ", Status: " + assinatura.getStatus());
                } else {
                    log.warning("Não foi possível obter detalhes completos da assinatura ID: " + assinatura.getId());
                    setDefaultSubscriptionData(model);
                }
            } else {
                // Tentar buscar pelo use case como fallback
                log.info("Assinatura não encontrada pelo caso de uso direto, tentando pelo use case padrão");
                Optional<PlanoAssinatura> assinaturaOptional = gerenciaAssinaturaUseCase.buscarAssinaturaPorUsuario(usuarioAtual);
                
                if (assinaturaOptional.isPresent()) {
                    log.info("Assinatura encontrada pelo use case padrão");
                    PlanoAssinatura assinatura = assinaturaOptional.get();
                    Plano plano = assinatura.getPlano();
                    
                    // Formatar datas
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String dataInicio = assinatura.getDataInicio().format(formatter);
                    String dataRenovacao = assinatura.getProximoPagamento() != null ? 
                                          assinatura.getProximoPagamento().format(formatter) : 
                                          "Não disponível";
                    
                    // Adicionar informações da assinatura ao modelo
                    model.addAttribute("planoAtual", plano.getNome());
                    model.addAttribute("dataInicio", dataInicio);
                    model.addAttribute("dataRenovacao", dataRenovacao);
                    model.addAttribute("statusAssinatura", assinatura.getStatus().toString());
                    model.addAttribute("valorMensal", "ECV " + plano.getPreco());
                    model.addAttribute("assinaturaId", assinatura.getId());
                    model.addAttribute("planoId", plano.getId());
                    
                    // Adicionar recursos do plano
                    String[] recursos = plano.getDescricao().split(",");
                    model.addAttribute("recursos", recursos);
                    
                    log.info("Dados da assinatura adicionados ao modelo: " + plano.getNome() + ", Status: " + assinatura.getStatus());
                } else {
                    log.info("Usuário não possui assinatura");
                    setDefaultSubscriptionData(model);
                }
            }
            
            // Buscar planos disponíveis para upgrade
            List<Plano> planosDisponiveis;
            try {
                planosDisponiveis = gerenciaPlanoUseCase.listarPlanosAtivos();
                log.info("Planos disponíveis carregados: " + planosDisponiveis.size());
            } catch (Exception e) {
                log.log(Level.WARNING, "Erro ao buscar planos disponíveis: " + e.getMessage(), e);
                planosDisponiveis = new ArrayList<>();
            }
            
            model.addAttribute("planosDisponiveis", planosDisponiveis);
            
            // Buscar histórico de pagamentos usando o caso de uso dedicado
            try {
                // Usar PageRequest sem ordenação para evitar conflito com a ordenação da consulta SQL
                Pageable pageable = PageRequest.of(page, size);
                Page<Pagamento> pagamentosPage = buscaHistoricoPagamentoUseCase.buscarHistoricoPagamentos(
                        usuarioAtual.getId(), null, pageable);
                
                log.info("Histórico de pagamentos carregado: " + pagamentosPage.getContent().size() + " registros");
                
                model.addAttribute("pagamentos", pagamentosPage.getContent());
                model.addAttribute("paginaAtual", pagamentosPage.getNumber());
                model.addAttribute("totalPaginas", pagamentosPage.getTotalPages());
                model.addAttribute("totalItens", pagamentosPage.getTotalElements());
            } catch (Exception e) {
                log.log(Level.SEVERE, "Erro ao buscar histórico de pagamentos: " + e.getMessage(), e);
                model.addAttribute("pagamentos", new ArrayList<Pagamento>());
                model.addAttribute("paginaAtual", 0);
                model.addAttribute("totalPaginas", 0);
                model.addAttribute("totalItens", 0);
            }
            
            // Adicionar URL base para links
            model.addAttribute("baseUrl", baseUrl);
            
            return "user/assinatura";
        } catch (Exception e) {
            log.log(Level.SEVERE, "Erro ao carregar página de assinatura: " + e.getMessage(), e);
            return "error";
        }
    }
    
    @PostMapping("/upgrade")
    public String upgradeAssinatura(@RequestParam("planoId") Long planoId, RedirectAttributes redirectAttributes, @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        try {
            log.info("Iniciando processo de upgrade para o plano ID: " + planoId);
            
            // Obter o usuário autenticado
            Usuario usuarioAtual = customOAuth2User.getUsuario();
            log.info("Usuário autenticado para upgrade: " + usuarioAtual.getEmail());
            
            // Verificar se o plano existe
            Optional<Plano> planoOptional = gerenciaPlanoUseCase.buscarPlanoPorId(planoId);
            if (planoOptional.isEmpty()) {
                log.warning("Plano não encontrado para ID: " + planoId);
                redirectAttributes.addFlashAttribute("error", "Plano não encontrado.");
                return "redirect:/user/assinatura";
            }
            
            Plano plano = planoOptional.get();
            
            // Verificar se o plano tem um stripePriceId válido
            if (plano.getStripePriceId() == null || plano.getStripePriceId().isEmpty()) {
                log.warning("Plano não possui um ID de preço do Stripe válido: " + planoId);
                redirectAttributes.addFlashAttribute("error", "Plano não configurado corretamente para pagamento.");
                return "redirect:/user/assinatura";
            }
            
            // Criar uma sessão de checkout do Stripe
            String successUrl = baseUrl + "/user/assinatura/checkout-success?planoId=" + planoId;
            String cancelUrl = baseUrl + "/user/assinatura/checkout-cancel";
            
            String checkoutUrl = stripeIntegrationUseCase.criarCheckoutSession(
                    usuarioAtual,
                    plano.getStripePriceId(),
                    successUrl,
                    cancelUrl
            );
            
            log.info("Sessão de checkout do Stripe criada com sucesso. Redirecionando para: " + checkoutUrl);
            
            // Redirecionar para a página de checkout do Stripe
            return "redirect:" + checkoutUrl;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Erro ao processar upgrade de assinatura: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Ocorreu um erro ao processar sua solicitação. Por favor, tente novamente.");
            return "redirect:/user/assinatura";
        }
    }
    
    @GetMapping("/checkout-success")
    public String checkoutSuccess(@RequestParam("planoId") Long planoId, RedirectAttributes redirectAttributes, @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        try {
            log.info("Checkout concluído com sucesso para o plano ID: " + planoId);
            
            // Obter o usuário autenticado
            Usuario usuarioAtual = customOAuth2User.getUsuario();
            
            // Buscar assinatura atual do usuário
            Optional<PlanoAssinatura> assinaturaOptional = buscaAssinaturaDirectaUseCase.buscarAssinaturaPorUsuarioId(usuarioAtual.getId());
            
            if (assinaturaOptional.isPresent()) {
                // Usuário já tem uma assinatura, realizar upgrade
                PlanoAssinatura assinaturaAtual = assinaturaOptional.get();
                
                // Verificar se não é o mesmo plano
                if (assinaturaAtual.getPlano().getId().equals(planoId)) {
                    log.warning("Usuário já possui este plano: " + planoId);
                    redirectAttributes.addFlashAttribute("warning", "Você já possui este plano.");
                    return "redirect:/user/assinatura";
                }
                
                // Realizar o upgrade da assinatura
                boolean sucesso = gerenciaAssinaturaUseCase.atualizarPlanoAssinatura(assinaturaAtual.getId(), planoId);
                
                if (sucesso) {
                    log.info("Upgrade realizado com sucesso para o plano ID: " + planoId);
                    redirectAttributes.addFlashAttribute("success", "Upgrade realizado com sucesso! Sua assinatura foi atualizada.");
                } else {
                    log.warning("Falha ao realizar upgrade para o plano ID: " + planoId);
                    redirectAttributes.addFlashAttribute("error", "Não foi possível realizar o upgrade. Por favor, tente novamente.");
                }
            } else {
                // Usuário não tem assinatura, criar uma nova
                boolean sucesso = gerenciaAssinaturaUseCase.criarNovaAssinatura(usuarioAtual, planoId);
                
                if (sucesso) {
                    log.info("Nova assinatura criada com sucesso para o plano ID: " + planoId);
                    redirectAttributes.addFlashAttribute("success", "Assinatura criada com sucesso!");
                } else {
                    log.warning("Falha ao criar nova assinatura para o plano ID: " + planoId);
                    redirectAttributes.addFlashAttribute("error", "Não foi possível criar a assinatura. Por favor, tente novamente.");
                }
            }
            
            return "redirect:/user/assinatura";
        } catch (Exception e) {
            log.log(Level.SEVERE, "Erro ao processar sucesso do checkout: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Ocorreu um erro ao processar sua solicitação. Por favor, tente novamente.");
            return "redirect:/user/assinatura";
        }
    }
    
    @GetMapping("/checkout-cancel")
    public String checkoutCancel(RedirectAttributes redirectAttributes) {
        log.info("Checkout cancelado pelo usuário");
        redirectAttributes.addFlashAttribute("warning", "Processo de assinatura cancelado. Nenhuma alteração foi realizada.");
        return "redirect:/user/assinatura";
    }
    
    @PostMapping("/cancelar")
    public String cancelarAssinatura(RedirectAttributes redirectAttributes, @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        try {
            log.info("Iniciando processo de cancelamento de assinatura");
            
            // Obter o usuário autenticado
            Usuario usuarioAtual = customOAuth2User.getUsuario();
            log.info("Usuário autenticado para cancelamento: " + usuarioAtual.getEmail());
            
            // Buscar assinatura atual do usuário
            Optional<PlanoAssinatura> assinaturaOptional = buscaAssinaturaDirectaUseCase.buscarAssinaturaPorUsuarioId(usuarioAtual.getId());
            
            if (assinaturaOptional.isPresent()) {
                PlanoAssinatura assinatura = assinaturaOptional.get();
                
                // Cancelar a assinatura
                boolean sucesso = gerenciaAssinaturaUseCase.cancelarAssinatura(assinatura.getId());
                
                if (sucesso) {
                    log.info("Assinatura cancelada com sucesso: " + assinatura.getId());
                    redirectAttributes.addFlashAttribute("success", "Sua assinatura foi cancelada com sucesso.");
                } else {
                    log.warning("Falha ao cancelar assinatura: " + assinatura.getId());
                    redirectAttributes.addFlashAttribute("error", "Não foi possível cancelar sua assinatura. Por favor, tente novamente.");
                }
            } else {
                log.warning("Usuário não possui assinatura para cancelar");
                redirectAttributes.addFlashAttribute("warning", "Você não possui uma assinatura ativa para cancelar.");
            }
            
            return "redirect:/user/assinatura";
        } catch (Exception e) {
            log.log(Level.SEVERE, "Erro ao processar cancelamento de assinatura: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Ocorreu um erro ao processar sua solicitação. Por favor, tente novamente.");
            return "redirect:/user/assinatura";
        }
    }
    
    // Configura dados padrão para usuários sem assinatura
    private void setDefaultSubscriptionData(Model model) {
        model.addAttribute("planoAtual", "Nenhum");
        model.addAttribute("dataInicio", "N/A");
        model.addAttribute("dataRenovacao", "N/A");
        model.addAttribute("statusAssinatura", "INATIVA");
        model.addAttribute("valorMensal", "ECV 0,00");
        model.addAttribute("assinaturaId", null);
        model.addAttribute("planoId", null);
        model.addAttribute("recursos", new String[]{"Nenhum recurso disponível"});
        log.info("Configurados dados padrão para usuário sem assinatura");
    }
}
