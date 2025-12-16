package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.TenantCreateRequest;
import cv.ntinnovation.vintipay.adapter.in.web.security.CustomOAuth2User;
import cv.ntinnovation.vintipay.adapter.out.persistence.adapter.UsuarioAdapter;
import cv.ntinnovation.vintipay.application.core.domain.entity.Tenant;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuario;
import cv.ntinnovation.vintipay.application.port.out.GerenciaTenantPort;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;

@Controller
@RequestMapping("/user/tenant")
@RequiredArgsConstructor
@Slf4j
public class UserTenantController {

    private final GerenciaTenantPort tenantPort;
    private final BuscaUsuario buscaUsuario;
    private final UsuarioAdapter usuarioAdapter;

    @GetMapping("/create")
    public String showCreateForm(Model model, Principal principal) {
        log.info("Exibindo formulário de criação de tenant");
        model.addAttribute("tenantRequest", new TenantCreateRequest());
        return "user/tenant-create";
    }

    @PostMapping("/create")
    public String createTenant(@Valid @ModelAttribute("tenantRequest") TenantCreateRequest request,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model,
                              HttpServletRequest httpServletRequest) {
        
        log.info("Recebendo requisição para criar tenant: {}", request.getNome());

        if (bindingResult.hasErrors()) {
            log.warn("Erros de validação ao criar tenant: {}", bindingResult.getAllErrors());
            model.addAttribute("erro", "Por favor, corrija os erros no formulário");
            return "user/tenant-create";
        }

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = null;

            if (authentication.getPrincipal() instanceof CustomOAuth2User customUser) {
                usuario = customUser.getUsuario();
            } else {
                throw new RuntimeException("Usuário não autenticado");
            }

            String slug = gerarSlug(request.getNome());
            
            if (tenantPort.existePorSlug(slug)) {
                log.warn("Tentativa de criar tenant com slug duplicado: {}", slug);
                model.addAttribute("erro", "Já existe uma organização com este nome");
                return "user/tenant-create";
            }

            Tenant tenant = Tenant.builder()
                    .codigo(UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .slug(slug)
                    .nome(request.getNome())
                    .nomeFantasia(request.getNomeFantasia())
                    .documento(request.getDocumento())
                    .email(request.getEmail())
                    .telefone(request.getTelefone())
                    .endereco(request.getEndereco())
                    .cidade(request.getCidade())
                    .pais(request.getPais())
                    .observacoes(request.getObservacoes())
                    .ativo(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            tenant = tenantPort.salvar(tenant);
            log.info("Tenant criado com sucesso: {} (ID: {})", tenant.getNome(), tenant.getId());

            // Associar tenant ao usuário diretamente via adapter (evita ciclo de mappers)
            usuarioAdapter.adicionarTenantAoUsuario(usuario.getId(), tenant.getId());
            log.info("Tenant associado ao usuário: {}", usuario.getEmail());

            redirectAttributes.addFlashAttribute("sucesso", "Organização criada com sucesso!");

            httpServletRequest.getSession().setAttribute("tenantId", tenant.getId());
            httpServletRequest.getSession().setAttribute("tenantSlug", tenant.getSlug());

            usuario.setTenants(new HashSet<>(tenantPort.buscarTenantPorUsuario(usuario.getId())));

            return "redirect:/user";

        } catch (Exception e) {
            log.error("Erro ao criar tenant: {}", e.getMessage(), e);
            model.addAttribute("erro", "Erro ao criar organização: " + e.getMessage());
            return "user/tenant-create";
        }
    }

    @GetMapping("/switch/{slug}")
    public String switchTenant(@PathVariable String slug, 
                              HttpServletRequest httpServletRequest,
                              RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication.getPrincipal() instanceof CustomOAuth2User customUser) {
                Usuario usuario = customUser.getUsuario();
                
                // Buscar tenant por slug
                Tenant tenant = tenantPort.buscarPorSlug(slug)
                        .orElseThrow(() -> new RuntimeException("Organização não encontrada"));
                
                // Verificar se o usuário tem acesso a este tenant
                boolean hasAccess = usuario.getTenants().stream()
                        .anyMatch(t -> t.getSlug().equals(slug));
                
                if (!hasAccess) {
                    log.warn("Usuário {} tentou acessar organização {} sem permissão", usuario.getEmail(), slug);
                    redirectAttributes.addFlashAttribute("erro", "Você não tem acesso a esta organização");
                    return "redirect:/user";
                }
                
                // Atualizar sessão com o novo tenant
                httpServletRequest.getSession().setAttribute("tenantId", tenant.getId());
                httpServletRequest.getSession().setAttribute("tenantSlug", tenant.getSlug());
                
                log.info("Usuário {} trocou para organização {}", usuario.getEmail(), tenant.getNome());
                redirectAttributes.addFlashAttribute("sucesso", "Organização alterada para: " + tenant.getNome());
                
            } else {
                throw new RuntimeException("Usuário não autenticado");
            }
            
            return "redirect:/user";
            
        } catch (Exception e) {
            log.error("Erro ao trocar organização: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("erro", "Erro ao trocar organização: " + e.getMessage());
            return "redirect:/user";
        }
    }

    private String gerarSlug(String nome) {
        return nome.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "_")
                .replaceAll("-+", "_")
                .replaceAll("^-|-$", "");
    }
}
