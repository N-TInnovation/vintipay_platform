package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.ConfiguracaoPagamentoEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.ConfiguracaoPagamentoRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.core.domain.rules.FingerPrintService;
import cv.ntinnovation.vintipay.application.core.service.CriptografiaService;
import cv.ntinnovation.vintipay.application.port.out.BuscaConfiguracaoPagamentoPort;
import cv.ntinnovation.vintipay.application.port.out.SalvaConfiguracaoPagamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@RequestMapping("/user/credenciais")
@RequiredArgsConstructor
public class UserCredenciaisController {

    private static final Logger logger = Logger.getLogger(UserCredenciaisController.class.getName());

    private final BuscaConfiguracaoPagamentoPort buscaConfiguracaoPagamentoPort;
    private final SalvaConfiguracaoPagamentoPort salvaConfiguracaoPagamentoPort;
    private final CriptografiaService criptografiaService;
    private final FingerPrintService fingerPrintService;
    private final ConfiguracaoPagamentoRepository configuracaoPagamentoRepository; // Acesso direto ao repositório

    @GetMapping("/{id}")
    public String exibirCredenciais(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        // Obter o usuário autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioAtual = (Usuario) authentication.getPrincipal();
        
        // Buscar configuração
        var configuracaoOpt = buscaConfiguracaoPagamentoPort.buscarPorId(id);
        
        if (configuracaoOpt.isPresent()) {
            ConfiguracaoPagamento configuracao = configuracaoOpt.get();
            
            // Verificar se a configuração pertence ao usuário
            if (configuracao.getUsuarioId() != null && configuracao.getUsuarioId().equals(usuarioAtual.getId())) {
                // Descriptografar o POS Aut Code para exibição
                try {
                    if (configuracao.getPosAutCode() != null && !configuracao.getPosAutCode().isEmpty()) {
                        String posAutCodeDescriptografado = fingerPrintService.decryptAES256(configuracao.getPosAutCode());
                        
                        // Usar o construtor Builder do Lombok para criar uma cópia com todos os campos
                        ConfiguracaoPagamento configExibicao = ConfiguracaoPagamento.builder()
                            .id(configuracao.getId())
                            .usuarioId(configuracao.getUsuarioId())
                            .nomeLoja(configuracao.getNomeLoja())
                            .merchantId(configuracao.getMerchantId())
                            .apiKey(configuracao.getApiKey())
                            .apiSecret(configuracao.getApiSecret())
                            .posId(configuracao.getPosId())
                            .posAutCode(posAutCodeDescriptografado) // Usar o valor descriptografado
                            .telefone(configuracao.getTelefone())
                            .email(configuracao.getEmail())
                            .urlLoja(configuracao.getUrlLoja())
                            .returnUrl(configuracao.getReturnUrl())
                            .returnAutCode(configuracao.getReturnAutCode())
                            .webhookUrl(configuracao.getWebhookUrl())
                            .webhookSecret(configuracao.getWebhookSecret())
                            .webhookAtivo(configuracao.getWebhookAtivo())
                            .returnAtivo(configuracao.getReturnAtivo())
                            .ativo(configuracao.getAtivo())
                            .isActive(configuracao.getIsActive())
                            .ambiente(configuracao.getAmbiente())
                            .observacoes(configuracao.getObservacoes())
                            .createdAt(configuracao.getCreatedAt())
                            .updatedAt(configuracao.getUpdatedAt())
                            .build();
                        
                        model.addAttribute("configuracao", configExibicao);
                    } else {
                        model.addAttribute("configuracao", configuracao);
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Erro ao descriptografar POS Aut Code: " + e.getMessage(), e);
                    model.addAttribute("configuracao", configuracao);
                    model.addAttribute("mensagemAlerta", "Não foi possível descriptografar o POS Aut Code. Exibindo valor criptografado.");
                }
                
                return "user/credenciais";
            }
        }
        
        // Redirecionar se não encontrar ou não pertencer ao usuário
        redirectAttributes.addFlashAttribute("mensagemErro", "Configuração não encontrada ou você não tem permissão para acessá-la.");
        return "redirect:/user/config-pagamentos";
    }
    
    /**
     * Método para renovar o API Secret (aceita tanto GET quanto POST)
     */
    @GetMapping("/renovar-api-secret/{id}")
    @Transactional
    public String renovarApiSecretGet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return renovarApiSecret(id, redirectAttributes);
    }
    
    @PostMapping("/renovar-api-secret/{id}")
    @Transactional
    public String renovarApiSecretPost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return renovarApiSecret(id, redirectAttributes);
    }
    
    /**
     * Método interno para renovar o API Secret
     */
    private String renovarApiSecret(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            logger.log(Level.INFO, "Iniciando renovação do API Secret para configuração ID: " + id);
            
            // Obter o usuário autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuarioAtual = (Usuario) authentication.getPrincipal();
            logger.log(Level.INFO, "Usuário autenticado: " + usuarioAtual.getUsername());
            
            // Buscar configuração diretamente do repositório
            Optional<ConfiguracaoPagamentoEntity> configuracaoEntityOpt = configuracaoPagamentoRepository.findById(id);
            
            if (configuracaoEntityOpt.isPresent()) {
                ConfiguracaoPagamentoEntity configuracaoEntity = configuracaoEntityOpt.get();
                logger.log(Level.INFO, "Configuração encontrada: " + configuracaoEntity.getId());
                
                // Verificar se a configuração pertence ao usuário
                if (configuracaoEntity.getUsuario() != null && 
                    configuracaoEntity.getUsuario().getId() != null && 
                    configuracaoEntity.getUsuario().getId().equals(usuarioAtual.getId())) {
                    
                    logger.log(Level.INFO, "Usuário tem permissão para renovar o API Secret");
                    
                    // Gerar novo API Secret
                    String novoApiSecret = gerarApiSecret();
                    logger.log(Level.INFO, "Novo API Secret gerado: " + novoApiSecret);
                    
                    // Criptografar o novo API Secret usando BCrypt (unidirecional)
                    String apiSecretCriptografado = criptografiaService.criptografar(novoApiSecret);
                    logger.log(Level.INFO, "API Secret criptografado com BCrypt: " + apiSecretCriptografado);
                    
                    // Atualizar a configuração diretamente na entidade
                    logger.log(Level.INFO, "API Secret original: " + configuracaoEntity.getApiSecret());
                    configuracaoEntity.setApiSecret(apiSecretCriptografado);
                    logger.log(Level.INFO, "API Secret atualizado: " + configuracaoEntity.getApiSecret());
                    configuracaoEntity.setUpdatedAt(LocalDateTime.now());
                    
                    // Salvar a entidade diretamente
                    logger.log(Level.INFO, "Salvando configuração atualizada diretamente no repositório...");
                    ConfiguracaoPagamentoEntity savedEntity = configuracaoPagamentoRepository.save(configuracaoEntity);
                    logger.log(Level.INFO, "Configuração salva com sucesso. ID: " + savedEntity.getId());
                    logger.log(Level.INFO, "API Secret após salvar: " + savedEntity.getApiSecret());
                    
                    // Verificar se o API Secret foi realmente atualizado
                    Optional<ConfiguracaoPagamentoEntity> verificacaoEntityOpt = configuracaoPagamentoRepository.findById(id);
                    if (verificacaoEntityOpt.isPresent()) {
                        String apiSecretSalvo = verificacaoEntityOpt.get().getApiSecret();
                        logger.log(Level.INFO, "API Secret verificado no banco: " + apiSecretSalvo);
                        
                        // Com BCrypt não podemos comparar diretamente os valores criptografados
                        // Verificamos apenas se o valor foi salvo
                        if (apiSecretSalvo == null || apiSecretSalvo.isEmpty()) {
                            logger.log(Level.WARNING, "API Secret não foi salvo no banco de dados!");
                            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao renovar API Secret: O valor não foi salvo.");
                            return "redirect:/user/credenciais/" + id;
                        } else {
                            logger.log(Level.INFO, "API Secret verificado e confirmado no banco de dados.");
                        }
                    } else {
                        logger.log(Level.WARNING, "Não foi possível verificar o API Secret após salvar!");
                    }
                    
                    // Exibir o novo API Secret não criptografado para o usuário
                    redirectAttributes.addFlashAttribute("novoApiSecret", novoApiSecret);
                    redirectAttributes.addFlashAttribute("mensagemSucesso", "API Secret renovado com sucesso! Guarde-o em um local seguro, pois ele não será exibido novamente.");
                    
                    return "redirect:/user/credenciais/" + id;
                } else {
                    logger.log(Level.WARNING, "Usuário não tem permissão para renovar o API Secret. Usuario ID: " + usuarioAtual.getId());
                }
            } else {
                logger.log(Level.WARNING, "Configuração não encontrada com ID: " + id);
            }
            
            // Redirecionar se não encontrar ou não pertencer ao usuário
            redirectAttributes.addFlashAttribute("mensagemErro", "Configuração não encontrada ou você não tem permissão para acessá-la.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao renovar API Secret: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao renovar API Secret: " + e.getMessage());
        }
        
        return "redirect:/user/config-pagamentos";
    }
    
    /**
     * Método para renovar o Return Aut Code
     */
    @PostMapping("/renovar-return-aut-code/{id}")
    @Transactional
    public String renovarReturnAutCode(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            logger.log(Level.INFO, "Iniciando renovação do Return Aut Code para configuração ID: " + id);
            
            // Obter o usuário autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuarioAtual = (Usuario) authentication.getPrincipal();
            logger.log(Level.INFO, "Usuário autenticado: " + usuarioAtual.getUsername());
            
            // Buscar configuração diretamente do repositório
            Optional<ConfiguracaoPagamentoEntity> configuracaoEntityOpt = configuracaoPagamentoRepository.findById(id);
            
            if (configuracaoEntityOpt.isPresent()) {
                ConfiguracaoPagamentoEntity configuracaoEntity = configuracaoEntityOpt.get();
                logger.log(Level.INFO, "Configuração encontrada: " + configuracaoEntity.getId());
                
                // Verificar se a configuração pertence ao usuário
                if (configuracaoEntity.getUsuario() != null && 
                    configuracaoEntity.getUsuario().getId() != null && 
                    configuracaoEntity.getUsuario().getId().equals(usuarioAtual.getId())) {
                    
                    logger.log(Level.INFO, "Usuário tem permissão para renovar o Return Aut Code");
                    
                    // Gerar novo Return Aut Code
                    String novoReturnAutCode = gerarSecret(16);
                    logger.log(Level.INFO, "Novo Return Aut Code gerado: " + novoReturnAutCode);
                    
                    // Criptografar o novo Return Aut Code usando BCrypt
                    String returnAutCodeCriptografado = criptografiaService.criptografar(novoReturnAutCode);
                    logger.log(Level.INFO, "Return Aut Code criptografado com BCrypt: " + returnAutCodeCriptografado);
                    
                    // Atualizar a configuração diretamente na entidade
                    logger.log(Level.INFO, "Return Aut Code original: " + configuracaoEntity.getReturnAutCode());
                    configuracaoEntity.setReturnAutCode(returnAutCodeCriptografado);
                    logger.log(Level.INFO, "Return Aut Code atualizado: " + configuracaoEntity.getReturnAutCode());
                    configuracaoEntity.setUpdatedAt(LocalDateTime.now());
                    
                    // Salvar a entidade diretamente
                    logger.log(Level.INFO, "Salvando configuração atualizada diretamente no repositório...");
                    ConfiguracaoPagamentoEntity savedEntity = configuracaoPagamentoRepository.save(configuracaoEntity);
                    logger.log(Level.INFO, "Configuração salva com sucesso. ID: " + savedEntity.getId());
                    logger.log(Level.INFO, "Return Aut Code após salvar: " + savedEntity.getReturnAutCode());
                    
                    // Exibir o novo Return Aut Code não criptografado para o usuário
                    redirectAttributes.addFlashAttribute("novoReturnAutCode", novoReturnAutCode);
                    redirectAttributes.addFlashAttribute("mensagemSucesso", "Return Aut Code renovado com sucesso! Guarde-o em um local seguro, pois ele não será exibido novamente.");
                    
                    return "redirect:/user/credenciais/" + id;
                } else {
                    logger.log(Level.WARNING, "Usuário não tem permissão para renovar o Return Aut Code. Usuario ID: " + usuarioAtual.getId());
                }
            } else {
                logger.log(Level.WARNING, "Configuração não encontrada com ID: " + id);
            }
            
            // Redirecionar se não encontrar ou não pertencer ao usuário
            redirectAttributes.addFlashAttribute("mensagemErro", "Configuração não encontrada ou você não tem permissão para acessá-la.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao renovar Return Aut Code: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao renovar Return Aut Code: " + e.getMessage());
        }
        
        return "redirect:/user/config-pagamentos";
    }
    
    /**
     * Método para renovar o Webhook Secret
     */
    @PostMapping("/renovar-webhook-secret/{id}")
    @Transactional
    public String renovarWebhookSecret(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            logger.log(Level.INFO, "Iniciando renovação do Webhook Secret para configuração ID: " + id);
            
            // Obter o usuário autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuarioAtual = (Usuario) authentication.getPrincipal();
            logger.log(Level.INFO, "Usuário autenticado: " + usuarioAtual.getUsername());
            
            // Buscar configuração diretamente do repositório
            Optional<ConfiguracaoPagamentoEntity> configuracaoEntityOpt = configuracaoPagamentoRepository.findById(id);
            
            if (configuracaoEntityOpt.isPresent()) {
                ConfiguracaoPagamentoEntity configuracaoEntity = configuracaoEntityOpt.get();
                logger.log(Level.INFO, "Configuração encontrada: " + configuracaoEntity.getId());
                
                // Verificar se a configuração pertence ao usuário
                if (configuracaoEntity.getUsuario() != null && 
                    configuracaoEntity.getUsuario().getId() != null && 
                    configuracaoEntity.getUsuario().getId().equals(usuarioAtual.getId())) {
                    
                    logger.log(Level.INFO, "Usuário tem permissão para renovar o Webhook Secret");
                    
                    // Gerar novo Webhook Secret
                    String novoWebhookSecret = gerarSecret(32);
                    logger.log(Level.INFO, "Novo Webhook Secret gerado: " + novoWebhookSecret);
                    
                    // Criptografar o novo Webhook Secret usando BCrypt
                    String webhookSecretCriptografado = criptografiaService.criptografar(novoWebhookSecret);
                    logger.log(Level.INFO, "Webhook Secret criptografado com BCrypt: " + webhookSecretCriptografado);
                    
                    // Atualizar a configuração diretamente na entidade
                    logger.log(Level.INFO, "Webhook Secret original: " + configuracaoEntity.getWebhookSecret());
                    configuracaoEntity.setWebhookSecret(webhookSecretCriptografado);
                    logger.log(Level.INFO, "Webhook Secret atualizado: " + configuracaoEntity.getWebhookSecret());
                    configuracaoEntity.setUpdatedAt(LocalDateTime.now());
                    
                    // Salvar a entidade diretamente
                    logger.log(Level.INFO, "Salvando configuração atualizada diretamente no repositório...");
                    ConfiguracaoPagamentoEntity savedEntity = configuracaoPagamentoRepository.save(configuracaoEntity);
                    logger.log(Level.INFO, "Configuração salva com sucesso. ID: " + savedEntity.getId());
                    logger.log(Level.INFO, "Webhook Secret após salvar: " + savedEntity.getWebhookSecret());
                    
                    // Verificar se o Webhook Secret foi realmente atualizado
                    Optional<ConfiguracaoPagamentoEntity> verificacaoEntityOpt = configuracaoPagamentoRepository.findById(id);
                    if (verificacaoEntityOpt.isPresent()) {
                        String webhookSecretSalvo = verificacaoEntityOpt.get().getWebhookSecret();
                        logger.log(Level.INFO, "Webhook Secret verificado no banco: " + webhookSecretSalvo);
                        
                        // Com BCrypt não podemos comparar diretamente os valores criptografados
                        // Verificamos apenas se o valor foi salvo
                        if (webhookSecretSalvo == null || webhookSecretSalvo.isEmpty()) {
                            logger.log(Level.WARNING, "Webhook Secret não foi salvo no banco de dados!");
                            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao renovar Webhook Secret: O valor não foi salvo.");
                            return "redirect:/user/credenciais/" + id;
                        } else {
                            logger.log(Level.INFO, "Webhook Secret verificado e confirmado no banco de dados.");
                        }
                    } else {
                        logger.log(Level.WARNING, "Não foi possível verificar o Webhook Secret após salvar!");
                    }
                    
                    // Exibir o novo Webhook Secret não criptografado para o usuário
                    redirectAttributes.addFlashAttribute("novoWebhookSecret", novoWebhookSecret);
                    redirectAttributes.addFlashAttribute("mensagemSucesso", "Webhook Secret renovado com sucesso! Guarde-o em um local seguro, pois ele não será exibido novamente.");
                    
                    return "redirect:/user/credenciais/" + id;
                } else {
                    logger.log(Level.WARNING, "Usuário não tem permissão para renovar o Webhook Secret. Usuario ID: " + usuarioAtual.getId());
                }
            } else {
                logger.log(Level.WARNING, "Configuração não encontrada com ID: " + id);
            }
            
            // Redirecionar se não encontrar ou não pertencer ao usuário
            redirectAttributes.addFlashAttribute("mensagemErro", "Configuração não encontrada ou você não tem permissão para acessá-la.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao renovar Webhook Secret: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao renovar Webhook Secret: " + e.getMessage());
        }
        
        return "redirect:/user/config-pagamentos";
    }
    
    /**
     * Gera um secret aleatório com o tamanho especificado
     * @param tamanho Tamanho do secret a ser gerado
     * @return Secret gerado
     */
    private String gerarSecret(int tamanho) {
        byte[] randomBytes = new byte[tamanho];
        new SecureRandom().nextBytes(randomBytes);
        
        // Converter para Base64
        String base64Secret = Base64.getEncoder().encodeToString(randomBytes);
        
        // Remover caracteres especiais e limitar tamanho
        String cleanSecret = base64Secret.replaceAll("[^a-zA-Z0-9]", "");
        
        // Garantir que a string tenha pelo menos o tamanho especificado
        if (cleanSecret.length() >= tamanho) {
            return cleanSecret.substring(0, tamanho);
        } else {
            // Se a string for menor que o tamanho especificado, preencher com caracteres adicionais
            StringBuilder secretBuilder = new StringBuilder(cleanSecret);
            String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            SecureRandom random = new SecureRandom();
            while (secretBuilder.length() < tamanho) {
                secretBuilder.append(chars.charAt(random.nextInt(chars.length())));
            }
            return secretBuilder.toString();
        }
    }
    
    /**
     * Gera um API Secret aleatório
     * @return API Secret gerado
     */
    private String gerarApiSecret() {
        return gerarSecret(32);
    }
}
