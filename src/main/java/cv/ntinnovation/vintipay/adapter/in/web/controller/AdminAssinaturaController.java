package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.out.persistence.repository.UsuarioRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.mapper.UsuarioMapper;
import cv.ntinnovation.vintipay.application.core.domain.entity.PlanoAssinatura;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.GerenciaAssinaturaUseCase;
import cv.ntinnovation.vintipay.application.port.in.GerenciaPlanoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/admin/assinaturas")
@RequiredArgsConstructor
public class AdminAssinaturaController {

    private final GerenciaAssinaturaUseCase gerenciaAssinaturaUseCase;
    private final GerenciaPlanoUseCase gerenciaPlanoUseCase;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @GetMapping
    public String listarAssinaturas(Model model) {
        log.debug("Listando todas as assinaturas");
        
        // Buscar todos os usuários
        List<Usuario> usuarios = usuarioRepository.findAll().stream()
                .map(usuarioMapper::toDomain)
                .collect(Collectors.toList());
        
        // Buscar assinaturas para cada usuário
        List<PlanoAssinatura> assinaturas = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            Optional<PlanoAssinatura> assinaturaOpt = gerenciaAssinaturaUseCase.buscarAssinaturaPorUsuario(usuario);
            assinaturaOpt.ifPresent(assinaturas::add);
        }
        
        model.addAttribute("assinaturas", assinaturas);
        return "admin/assinaturas/lista";
    }

    @GetMapping("/{id}")
    public String detalharAssinatura(@PathVariable Long id, Model model) {
        log.debug("Exibindo detalhes da assinatura ID: {}", id);
        
        // Buscar o usuário pelo ID
        Usuario usuario = usuarioRepository.findById(id)
                .map(usuarioMapper::toDomain)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        // Buscar a assinatura do usuário
        PlanoAssinatura assinatura = gerenciaAssinaturaUseCase.buscarAssinaturaPorUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Assinatura não encontrada"));
        
        model.addAttribute("assinatura", assinatura);
        return "admin/assinaturas/detalhes";
    }

    @GetMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('ADMIN')")
    public String cancelarAssinatura(@PathVariable Long id) {
        log.debug("Cancelando assinatura do usuário ID: {}", id);
        
        try {
            // Buscar o usuário pelo ID
            Usuario usuario = usuarioRepository.findById(id)
                    .map(usuarioMapper::toDomain)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            // Cancelar a assinatura
            gerenciaAssinaturaUseCase.cancelarAssinatura(usuario);
            return "redirect:/admin/assinaturas?sucesso=Assinatura+cancelada+com+sucesso";
        } catch (Exception e) {
            log.error("Erro ao cancelar assinatura", e);
            return "redirect:/admin/assinaturas?erro=" + e.getMessage();
        }
    }

    @GetMapping("/{id}/renovar")
    @PreAuthorize("hasRole('ADMIN')")
    public String renovarAssinatura(@PathVariable Long id) {
        log.debug("Renovando assinatura do usuário ID: {}", id);
        
        try {
            // Buscar o usuário pelo ID
            Usuario usuario = usuarioRepository.findById(id)
                    .map(usuarioMapper::toDomain)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            // Buscar a assinatura do usuário
            PlanoAssinatura assinatura = gerenciaAssinaturaUseCase.buscarAssinaturaPorUsuario(usuario)
                    .orElseThrow(() -> new RuntimeException("Assinatura não encontrada"));
            
            // Renovar a assinatura se tiver um ID externo do Stripe
            if (assinatura.getStripeSubscriptionId() != null && !assinatura.getStripeSubscriptionId().isEmpty()) {
                gerenciaAssinaturaUseCase.renovarAssinatura(assinatura.getStripeSubscriptionId());
                return "redirect:/admin/assinaturas?sucesso=Assinatura+renovada+com+sucesso";
            } else {
                return "redirect:/admin/assinaturas?erro=Não+foi+possível+renovar+a+assinatura+sem+ID+externo";
            }
        } catch (Exception e) {
            log.error("Erro ao renovar assinatura", e);
            return "redirect:/admin/assinaturas?erro=" + e.getMessage();
        }
    }

    @GetMapping("/nova")
    @PreAuthorize("hasRole('ADMIN')")
    public String exibirFormularioNovaAssinatura(@RequestParam(required = false) Long usuario, Model model) {
        log.debug("Exibindo formulário para nova assinatura, usuário ID: {}", usuario);
        
        // Buscar todos os planos ativos
        var planos = gerenciaPlanoUseCase.listarPlanosAtivos();
        model.addAttribute("planos", planos);
        
        // Se um ID de usuário foi fornecido, buscar o usuário
        if (usuario != null) {
            var usuarioOpt = usuarioRepository.findById(usuario)
                    .map(usuarioMapper::toDomain);
            
            if (usuarioOpt.isPresent()) {
                model.addAttribute("usuario", usuarioOpt.get());
                
                // Verificar se o usuário já tem uma assinatura
                var assinaturaOpt = gerenciaAssinaturaUseCase.buscarAssinaturaPorUsuario(usuarioOpt.get());
                if (assinaturaOpt.isPresent()) {
                    model.addAttribute("assinaturaExistente", assinaturaOpt.get());
                }
            }
        }
        
        // Buscar todos os usuários para o caso de não ter sido fornecido um ID
        if (usuario == null) {
            var usuarios = usuarioRepository.findAll().stream()
                    .map(usuarioMapper::toDomain)
                    .collect(Collectors.toList());
            model.addAttribute("usuarios", usuarios);
        }
        
        return "admin/assinaturas/form";
    }
    
    @PostMapping("/nova")
    @PreAuthorize("hasRole('ADMIN')")
    public String criarNovaAssinatura(@RequestParam Long usuario, 
                                     @RequestParam Long plano,
                                     @RequestParam(required = false) String stripeCustomerId,
                                     @RequestParam(required = false) String stripeSubscriptionId,
                                     @RequestParam(defaultValue = "false") boolean skipStripeIntegration,
                                     Model model) {
        log.debug("Criando nova assinatura para usuário ID: {}, plano ID: {}, skipStripeIntegration: {}", 
                 usuario, plano, skipStripeIntegration);
        
        try {
            // Buscar o usuário
            var usuarioOpt = usuarioRepository.findById(usuario)
                    .map(usuarioMapper::toDomain);
            
            if (usuarioOpt.isEmpty()) {
                throw new IllegalArgumentException("Usuário não encontrado");
            }
            
            // Verificar se o usuário já tem uma assinatura
            var assinaturaExistente = gerenciaAssinaturaUseCase.buscarAssinaturaPorUsuario(usuarioOpt.get());
            
            if (assinaturaExistente.isPresent()) {
                // Se já existe uma assinatura, perguntar se deseja substituir
                return "redirect:/admin/assinaturas/nova?usuario=" + usuario + "&erro=Usuário+já+possui+assinatura.+Cancele+a+assinatura+atual+antes+de+criar+uma+nova.";
            }
            
            // Criar a assinatura
            PlanoAssinatura assinatura = gerenciaAssinaturaUseCase.criarAssinatura(
                    usuarioOpt.get(), 
                    plano,
                    skipStripeIntegration
            );
            
            return "redirect:/admin/assinaturas?sucesso=Assinatura+criada+com+sucesso";
        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação ao criar assinatura: {}", e.getMessage());
            return "redirect:/admin/assinaturas/nova?usuario=" + usuario + "&erro=" + e.getMessage();
        } catch (Exception e) {
            log.error("Erro ao criar assinatura", e);
            return "redirect:/admin/assinaturas/nova?usuario=" + usuario + "&erro=Erro+ao+criar+assinatura:+" + e.getMessage();
        }
    }
}
