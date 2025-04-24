package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.CriaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaConfiguracaoPagamentoPort;
import cv.ntinnovation.vintipay.application.port.out.SalvaConfiguracaoPagamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/user/config-pagamentos")
@RequiredArgsConstructor
public class UserConfigPagamentosController {

    private final BuscaConfiguracaoPagamentoPort buscaConfiguracaoPagamentoPort;
    private final SalvaConfiguracaoPagamentoPort salvaConfiguracaoPagamentoPort;
    private final CriaConfiguracaoPagamentoUseCase criaConfiguracaoPagamentoUseCase;

    @GetMapping({"", "/"})
    public String configPagamentosPage(Model model) {
        // Obter o usuário autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioAtual = (Usuario) authentication.getPrincipal();
        
        // Adicionar dados do usuário ao modelo
        model.addAttribute("usuario", usuarioAtual);
        
        // Buscar configurações de pagamento do usuário
        List<ConfiguracaoPagamento> configuracoesPagamento = buscaConfiguracaoPagamentoPort.buscarPorUsuario(usuarioAtual.getId());
        model.addAttribute("configuracoesPagamento", configuracoesPagamento);
        
        // Criar objeto para nova configuração
        model.addAttribute("novaConfiguracao", new ConfiguracaoPagamento());
        
        return "user/config-pagamentos";
    }
    
    @PostMapping("/salvar")
    public String salvarConfiguracao(
            @ModelAttribute ConfiguracaoPagamento configuracao,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Obter o usuário autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuarioAtual = (Usuario) authentication.getPrincipal();
            
            // Configurar dados do usuário
            configuracao.setUsuario(usuarioAtual);
            configuracao.setUsuarioId(usuarioAtual.getId());
            
            // Configurar datas
            if (configuracao.getId() == null) {
                configuracao.setCreatedAt(LocalDateTime.now());
                
                // Gerar API Key e API Secret automaticamente para novas configurações
                String apiKey = gerarApiKey(usuarioAtual.getId(), configuracao.getPosId());
                String apiSecret = gerarApiSecret();
                
                configuracao.setApiKey(apiKey);
                configuracao.setApiSecret(apiSecret);
                
                // Gerar Return Aut Code automaticamente
                String returnAutCode = gerarReturnAutCode();
                configuracao.setReturnAutCode(returnAutCode);
                
                // Gerar Webhook Secret automaticamente
                String webhookSecret = gerarWebhookSecret();
                configuracao.setWebhookSecret(webhookSecret);
            }
            configuracao.setUpdatedAt(LocalDateTime.now());
            
            // Definir como ativo por padrão
            if (configuracao.getAtivo() == null) {
                configuracao.setAtivo(true);
            }
            if (configuracao.getIsActive() == null) {
                configuracao.setIsActive(true);
            }
            
            // Salvar configuração
            if (configuracao.getId() == null) {
                criaConfiguracaoPagamentoUseCase.criar(configuracao);
                redirectAttributes.addFlashAttribute("mensagemSucesso", "Configuração de pagamento SISP criada com sucesso!");
            } else {
                salvaConfiguracaoPagamentoPort.salvar(configuracao);
                redirectAttributes.addFlashAttribute("mensagemSucesso", "Configuração de pagamento SISP atualizada com sucesso!");
            }
        } catch (Exception e) {
            // Adicionar mensagem de erro
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao salvar configuração: " + e.getMessage());
        }
        
        return "redirect:/user/config-pagamentos";
    }
    
    @GetMapping("/editar/{id}")
    public String editarConfiguracao(@PathVariable Long id, Model model) {
        // Obter o usuário autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioAtual = (Usuario) authentication.getPrincipal();
        
        // Buscar configuração
        var configuracaoOpt = buscaConfiguracaoPagamentoPort.buscarPorId(id);
        
        if (configuracaoOpt.isPresent()) {
            ConfiguracaoPagamento configuracao = configuracaoOpt.get();
            
            // Verificar se a configuração pertence ao usuário
            if (configuracao.getUsuarioId() != null && configuracao.getUsuarioId().equals(usuarioAtual.getId())) {
                model.addAttribute("configuracao", configuracao);
                model.addAttribute("usuario", usuarioAtual);
                return "user/editar-configuracao";
            }
        }
        
        // Redirecionar se não encontrar ou não pertencer ao usuário
        return "redirect:/user/config-pagamentos";
    }
    
    @PostMapping("/ativar/{id}")
    public String ativarConfiguracao(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Obter o usuário autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuarioAtual = (Usuario) authentication.getPrincipal();
            
            // Buscar configuração
            var configuracaoOpt = buscaConfiguracaoPagamentoPort.buscarPorId(id);
            
            if (configuracaoOpt.isPresent()) {
                ConfiguracaoPagamento configuracao = configuracaoOpt.get();
                
                // Verificar se a configuração pertence ao usuário
                if (configuracao.getUsuarioId() != null && configuracao.getUsuarioId().equals(usuarioAtual.getId())) {
                    // Atualizar status
                    configuracao.setAtivo(true);
                    configuracao.setIsActive(true);
                    configuracao.setUpdatedAt(LocalDateTime.now());
                    
                    // Salvar
                    salvaConfiguracaoPagamentoPort.salvar(configuracao);
                    redirectAttributes.addFlashAttribute("mensagemSucesso", "Configuração ativada com sucesso!");
                }
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao ativar configuração: " + e.getMessage());
        }
        
        return "redirect:/user/config-pagamentos";
    }
    
    @PostMapping("/desativar/{id}")
    public String desativarConfiguracao(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Obter o usuário autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuarioAtual = (Usuario) authentication.getPrincipal();
            
            // Buscar configuração
            var configuracaoOpt = buscaConfiguracaoPagamentoPort.buscarPorId(id);
            
            if (configuracaoOpt.isPresent()) {
                ConfiguracaoPagamento configuracao = configuracaoOpt.get();
                
                // Verificar se a configuração pertence ao usuário
                if (configuracao.getUsuarioId() != null && configuracao.getUsuarioId().equals(usuarioAtual.getId())) {
                    // Atualizar status
                    configuracao.setAtivo(false);
                    configuracao.setIsActive(false);
                    configuracao.setUpdatedAt(LocalDateTime.now());
                    
                    // Salvar
                    salvaConfiguracaoPagamentoPort.salvar(configuracao);
                    redirectAttributes.addFlashAttribute("mensagemSucesso", "Configuração desativada com sucesso!");
                }
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao desativar configuração: " + e.getMessage());
        }
        
        return "redirect:/user/config-pagamentos";
    }
    
    @PostMapping("/excluir/{id}")
    public String excluirConfiguracao(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // Neste exemplo, apenas desativamos a configuração em vez de excluí-la
        return desativarConfiguracao(id, redirectAttributes);
    }
    
    // Métodos auxiliares para geração de API Key e API Secret
    
    /**
     * Gera uma API Key baseada no ID do usuário e POS ID
     * @param usuarioId ID do usuário
     * @param posId POS ID
     * @return API Key gerada
     */
    private String gerarApiKey(Long usuarioId, String posId) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String baseString = "VINTIPAY_" + usuarioId + "_" + posId + "_" + timestamp;
        
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(baseString.getBytes(StandardCharsets.UTF_8));
            
            // Converter para hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            // Retornar os primeiros 32 caracteres
            return hexString.toString().substring(0, 32).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            // Fallback se o algoritmo não estiver disponível
            return "API_" + usuarioId + "_" + timestamp.substring(timestamp.length() - 8);
        }
    }
    
    /**
     * Gera um API Secret aleatório
     * @return API Secret gerado
     */
    private String gerarApiSecret() {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        
        // Converter para Base64
        String base64Secret = Base64.getEncoder().encodeToString(randomBytes);
        
        // Remover caracteres especiais e limitar tamanho
        return base64Secret.replaceAll("[^a-zA-Z0-9]", "").substring(0, 32);
    }
    
    /**
     * Gera um Return Aut Code aleatório
     * @return Return Aut Code gerado
     */
    private String gerarReturnAutCode() {
        // Gerar código alfanumérico de 16 caracteres
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder returnAutCode = new StringBuilder();
        SecureRandom random = new SecureRandom();
        
        for (int i = 0; i < 16; i++) {
            int index = random.nextInt(caracteres.length());
            returnAutCode.append(caracteres.charAt(index));
        }
        
        return returnAutCode.toString();
    }
    
    /**
     * Gera um Webhook Secret aleatório
     * @return Webhook Secret gerado
     */
    private String gerarWebhookSecret() {
        // Gerar um token seguro para webhook de 32 caracteres
        byte[] randomBytes = new byte[24];
        SecureRandom random = new SecureRandom();
        random.nextBytes(randomBytes);
        
        // Converter para Base64 e remover caracteres especiais
        String base64Secret = Base64.getEncoder().encodeToString(randomBytes);
        return base64Secret.replaceAll("[^a-zA-Z0-9]", "").substring(0, 32);
    }
}
