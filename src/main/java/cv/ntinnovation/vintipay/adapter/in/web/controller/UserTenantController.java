package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.TenantCreateRequest;
import cv.ntinnovation.vintipay.adapter.in.web.security.CustomOAuth2User;
import cv.ntinnovation.vintipay.adapter.out.persistence.adapter.UsuarioAdapter;
import cv.ntinnovation.vintipay.application.core.domain.entity.Tenant;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuario;
import cv.ntinnovation.vintipay.application.port.out.GerenciaTenantPort;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
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
    public String showCreateForm(Model model) {
        log.info("Exibindo formulário de criação de tenant");
        model.addAttribute("tenantRequest", new TenantCreateRequest());
        return "user/tenant-create";
    }

    @PostMapping("/create")
    public String createTenant(@Valid @ModelAttribute("tenantRequest") TenantCreateRequest request,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        
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
                usuario = buscaUsuario.buscarPorEmail(customUser.getEmail())
                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
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
            return "redirect:/user";

        } catch (Exception e) {
            log.error("Erro ao criar tenant: {}", e.getMessage(), e);
            model.addAttribute("erro", "Erro ao criar organização: " + e.getMessage());
            return "user/tenant-create";
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
