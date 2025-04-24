package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.out.db.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.adapter.out.db.mapper.UsusarioMapper;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.GerenciaUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/admin/perfil")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class AdminPerfilController {

    private final GerenciaUsuarioUseCase gerenciaUsuarioUseCase;
    private final UsusarioMapper usuarioMapper;

    @GetMapping
    public String visualizarPerfil(Model model, RedirectAttributes redirectAttributes) {
        try {
            // Obter o usuário autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = "";
            
            if (authentication.getPrincipal() instanceof UserDetails) {
                username = ((UserDetails) authentication.getPrincipal()).getUsername();
            } else {
                username = authentication.getPrincipal().toString();
            }
            
            // Buscar o usuário pelo username
            Optional<Usuario> usuarioOptional = gerenciaUsuarioUseCase.buscarPorUsername(username);
            if (usuarioOptional.isEmpty()) {
                redirectAttributes.addFlashAttribute("erro", "Usuário não encontrado");
                return "redirect:/admin/dashboard";
            }
            
            Usuario usuario = usuarioOptional.get();
            model.addAttribute("usuario", usuario);
            
            return "admin/perfil/index";
        } catch (Exception e) {
            log.error("Erro ao visualizar perfil", e);
            redirectAttributes.addFlashAttribute("erro", "Erro ao visualizar perfil: " + e.getMessage());
            return "redirect:/admin/dashboard";
        }
    }
    
    @GetMapping("/editar")
    public String editarPerfil(Model model, RedirectAttributes redirectAttributes) {
        try {
            // Obter o usuário autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = "";
            
            if (authentication.getPrincipal() instanceof UserDetails) {
                username = ((UserDetails) authentication.getPrincipal()).getUsername();
            } else {
                username = authentication.getPrincipal().toString();
            }
            
            // Buscar o usuário pelo username
            Optional<Usuario> usuarioOptional = gerenciaUsuarioUseCase.buscarPorUsername(username);
            if (usuarioOptional.isEmpty()) {
                redirectAttributes.addFlashAttribute("erro", "Usuário não encontrado");
                return "redirect:/admin/dashboard";
            }
            
            Usuario usuario = usuarioOptional.get();
            model.addAttribute("usuario", usuario);
            
            return "admin/perfil/form";
        } catch (Exception e) {
            log.error("Erro ao editar perfil", e);
            redirectAttributes.addFlashAttribute("erro", "Erro ao editar perfil: " + e.getMessage());
            return "redirect:/admin/perfil";
        }
    }
    
    @PostMapping("/atualizar")
    public String atualizarPerfil(@ModelAttribute("usuario") UsuarioEntity usuarioForm,
                                 @RequestParam(required = false) String novaPassword,
                                 @RequestParam(required = false) String confirmPassword,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Obter o usuário autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = "";
            
            if (authentication.getPrincipal() instanceof UserDetails) {
                username = ((UserDetails) authentication.getPrincipal()).getUsername();
            } else {
                username = authentication.getPrincipal().toString();
            }
            
            // Buscar o usuário pelo username
            Optional<Usuario> usuarioOptional = gerenciaUsuarioUseCase.buscarPorUsername(username);
            if (usuarioOptional.isEmpty()) {
                redirectAttributes.addFlashAttribute("erro", "Usuário não encontrado");
                return "redirect:/admin/dashboard";
            }
            
            Usuario usuarioAtual = usuarioOptional.get();
            
            // Criar um novo objeto Usuario com os dados atualizados
            Usuario usuarioAtualizado = Usuario.builder(
                usuarioAtual.getUsername(),
                usuarioForm.getEmail(),
                usuarioForm.getNome(),
                usuarioAtual.getPassword(),
                usuarioAtual.isAtivo(),
                usuarioAtual.getRole()
            );
            
            // Configurar outros campos
            usuarioAtualizado.setId(usuarioAtual.getId());
            usuarioAtualizado.setTelefone(usuarioForm.getTelefone());
            usuarioAtualizado.setStripeCustomerId(usuarioAtual.getStripeCustomerId());
            usuarioAtualizado.setDataRegistro(usuarioAtual.getDataRegistro());
            usuarioAtualizado.setUltimoLogin(usuarioAtual.getUltimoLogin());
            
            // Verificar se o usuário deseja alterar a senha
            if (novaPassword != null && !novaPassword.isEmpty()) {
                // Validar se as senhas coincidem
                if (!novaPassword.equals(confirmPassword)) {
                    redirectAttributes.addFlashAttribute("erro", "As senhas não coincidem");
                    return "redirect:/admin/perfil/editar";
                }
                
                // Definir a nova senha
                usuarioAtualizado.setPassword(novaPassword);
            }
            
            // Salvar as alterações
            gerenciaUsuarioUseCase.atualizarUsuario(usuarioAtualizado);
            
            redirectAttributes.addFlashAttribute("sucesso", "Perfil atualizado com sucesso");
            return "redirect:/admin/perfil";
        } catch (Exception e) {
            log.error("Erro ao atualizar perfil", e);
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar perfil: " + e.getMessage());
            return "redirect:/admin/perfil/editar";
        }
    }
}
