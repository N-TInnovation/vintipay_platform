package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.out.db.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.adapter.out.db.mapper.UsuarioMapper;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.PlanoAssinatura;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.GerenciaAssinaturaUseCase;
import cv.ntinnovation.vintipay.application.port.in.GerenciaUsuarioUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaConfiguracaoPagamentoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Slf4j
@Controller
@RequestMapping("/admin/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioAdminController {

    private final GerenciaUsuarioUseCase gerenciaUsuarioUseCase;
    private final GerenciaAssinaturaUseCase gerenciaAssinaturaUseCase;
    private final UsuarioMapper usuarioMapper;
    private final BuscaConfiguracaoPagamentoPort buscaConfiguracaoPagamentoPort;

    @GetMapping
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = gerenciaUsuarioUseCase.listarTodos();
        
        // Buscar assinaturas para cada usuário
        Map<Long, Optional<PlanoAssinatura>> assinaturasMap = new HashMap<>();
        for (Usuario usuario : usuarios) {
            assinaturasMap.put(usuario.getId(), gerenciaAssinaturaUseCase.buscarAssinaturaPorUsuario(usuario));
        }
        
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("assinaturasMap", assinaturasMap);
        return "admin/usuarios/lista";
    }

    @GetMapping("/{id}")
    public String visualizarUsuario(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Usuario> usuarioOptional = gerenciaUsuarioUseCase.buscarPorId(id);
            if (usuarioOptional.isEmpty()) {
                redirectAttributes.addAttribute("erro", "Usuário não encontrado");
                return "redirect:/admin/usuarios";
            }
            
            Usuario usuario = usuarioOptional.get();
            
            // Garantir que o array de roles não seja nulo
            if (usuario.getRole() == null) {
                usuario.setRole(new String[]{"USER"});
            }
            
            model.addAttribute("usuario", usuario);
            
            // Buscar assinatura do usuário, se existir
            Optional<PlanoAssinatura> assinaturaOptional = gerenciaAssinaturaUseCase.buscarAssinaturaPorUsuario(usuario);
            assinaturaOptional.ifPresent(assinatura -> model.addAttribute("assinatura", assinatura));
            
            // Buscar configurações de pagamento do usuário
            List<ConfiguracaoPagamento> configuracoes = buscaConfiguracaoPagamentoPort.buscarPorUsuario(usuario.getId());
            model.addAttribute("configuracoes", configuracoes);
            
            return "admin/usuarios/detalhes";
        } catch (Exception e) {
            log.error("Erro ao visualizar usuário", e);
            redirectAttributes.addAttribute("erro", "Erro ao visualizar usuário: " + e.getMessage());
            return "redirect:/admin/usuarios";
        }
    }

    @GetMapping("/novo")
    public String formularioNovoUsuario(Model model) {
        // Criar um objeto UsuarioEntity vazio para o formulário
        // Não podemos usar Usuario.builder com valores vazios
        UsuarioEntity usuarioEntity = new UsuarioEntity();
        usuarioEntity.setAtivo(true);
        model.addAttribute("usuario", usuarioEntity);
        return "admin/usuarios/form";
    }

    @PostMapping("/novo")
    public String salvarNovoUsuario(@ModelAttribute("usuario") UsuarioEntity usuarioForm, 
                                   @RequestParam String confirmPassword,
                                   @RequestParam(required = false) List<String> roles,
                                   RedirectAttributes redirectAttributes) {
        try {
            // Validar se as senhas coincidem
            if (!usuarioForm.getPassword().equals(confirmPassword)) {
                redirectAttributes.addAttribute("erro", "As senhas não coincidem");
                return "redirect:/admin/usuarios/novo";
            }
            
            // Processar roles - remover prefixo ROLE_ se existir
            String[] rolesArray = new String[0];
            if (roles != null && !roles.isEmpty()) {
                rolesArray = roles.stream()
                    .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                    .toArray(String[]::new);
            } else {
                rolesArray = new String[]{"USER"};
            }
            
            // Criar um usuário de domínio com valores válidos para o Spring Security
            Usuario usuario = Usuario.builder(
                usuarioForm.getUsername() != null ? usuarioForm.getUsername() : "temp_" + System.currentTimeMillis(),
                usuarioForm.getEmail() != null ? usuarioForm.getEmail() : "",
                usuarioForm.getNome() != null ? usuarioForm.getNome() : "",
                usuarioForm.getPassword() != null ? usuarioForm.getPassword() : "temp_password",
                usuarioForm.isAtivo(),
                rolesArray
            );
            usuario.setTelefone(usuarioForm.getTelefone());
            
            // Salvar o usuário usando o caso de uso
            gerenciaUsuarioUseCase.criarUsuario(usuario);
            
            redirectAttributes.addAttribute("sucesso", "Usuário criado com sucesso");
            return "redirect:/admin/usuarios";
        } catch (IllegalArgumentException e) {
            log.error("Erro ao criar usuário", e);
            redirectAttributes.addAttribute("erro", e.getMessage());
            return "redirect:/admin/usuarios/novo";
        } catch (Exception e) {
            log.error("Erro ao criar usuário", e);
            redirectAttributes.addAttribute("erro", "Erro ao criar usuário: " + e.getMessage());
            return "redirect:/admin/usuarios/novo";
        }
    }

    @GetMapping("/{id}/editar")
    public String formularioEditarUsuario(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Usuario> usuarioOptional = gerenciaUsuarioUseCase.buscarPorId(id);
            if (usuarioOptional.isEmpty()) {
                redirectAttributes.addAttribute("erro", "Usuário não encontrado");
                return "redirect:/admin/usuarios";
            }
            
            // Converter o Usuario para UsuarioEntity para o formulário
            Usuario usuario = usuarioOptional.get();
            
            // Garantir que o array de roles não seja nulo
            if (usuario.getRole() == null) {
                usuario.setRole(new String[]{"USER"});
            }
            
            UsuarioEntity usuarioEntity = usuarioMapper.toEntity(usuario);
            model.addAttribute("usuario", usuarioEntity);
            return "admin/usuarios/form";
        } catch (Exception e) {
            log.error("Erro ao carregar formulário de edição", e);
            redirectAttributes.addAttribute("erro", "Erro ao carregar formulário de edição: " + e.getMessage());
            return "redirect:/admin/usuarios";
        }
    }

    @PostMapping("/{id}/editar")
    public String atualizarUsuario(@PathVariable Long id, 
                                  @ModelAttribute("usuario") UsuarioEntity usuarioForm,
                                  @RequestParam(required = false) String password,
                                  @RequestParam(required = false) String confirmPassword,
                                  @RequestParam(required = false) List<String> roles,
                                  RedirectAttributes redirectAttributes) {
        try {
            Optional<Usuario> usuarioOptional = gerenciaUsuarioUseCase.buscarPorId(id);
            if (usuarioOptional.isEmpty()) {
                redirectAttributes.addAttribute("erro", "Usuário não encontrado");
                return "redirect:/admin/usuarios";
            }
            
            Usuario usuario = usuarioOptional.get();
            
            // Atualizar dados básicos - garantindo que não haja valores nulos
            usuario.setNome(usuarioForm.getNome() != null ? usuarioForm.getNome() : "");
            usuario.setEmail(usuarioForm.getEmail() != null ? usuarioForm.getEmail() : "");
            usuario.setTelefone(usuarioForm.getTelefone());
            usuario.setAtivo(usuarioForm.isAtivo());
            
            // Atualizar username se foi alterado e não for nulo
            if (usuarioForm.getUsername() != null && !usuario.getUsername().equals(usuarioForm.getUsername())) {
                usuario.setUsername(usuarioForm.getUsername());
            }
            
            // Atualizar senha se fornecida
            if (password != null && !password.isEmpty()) {
                if (!password.equals(confirmPassword)) {
                    redirectAttributes.addAttribute("erro", "As senhas não coincidem");
                    return "redirect:/admin/usuarios/" + id + "/editar";
                }
                usuario.setPassword(password);
            }
            
            // Atualizar roles
            if (roles != null && !roles.isEmpty()) {
                // Remover o prefixo ROLE_ se existir, pois o builder já adiciona
                String[] rolesArray = roles.stream()
                    .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                    .toArray(String[]::new);
                
                usuario.setRole(rolesArray);
            } else {
                // Garantir que o usuário tenha pelo menos o papel USER
                usuario.setRole(new String[]{"USER"});
            }
            
            // Salvar o usuário usando o caso de uso
            gerenciaUsuarioUseCase.atualizarUsuario(usuario);
            
            redirectAttributes.addAttribute("sucesso", "Usuário atualizado com sucesso");
            return "redirect:/admin/usuarios";
        } catch (IllegalArgumentException e) {
            log.error("Erro ao atualizar usuário", e);
            redirectAttributes.addAttribute("erro", e.getMessage());
            return "redirect:/admin/usuarios/" + id + "/editar";
        } catch (Exception e) {
            log.error("Erro ao atualizar usuário", e);
            redirectAttributes.addAttribute("erro", "Erro ao atualizar usuário: " + e.getMessage());
            return "redirect:/admin/usuarios/" + id + "/editar";
        }
    }

    @PostMapping("/{id}/ativar")
    public String ativarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            gerenciaUsuarioUseCase.ativarUsuario(id);
            redirectAttributes.addAttribute("sucesso", "Usuário ativado com sucesso");
            return "redirect:/admin/usuarios";
        } catch (Exception e) {
            log.error("Erro ao ativar usuário", e);
            redirectAttributes.addAttribute("erro", "Erro ao ativar usuário: " + e.getMessage());
            return "redirect:/admin/usuarios";
        }
    }

    @PostMapping("/{id}/desativar")
    public String desativarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            gerenciaUsuarioUseCase.desativarUsuario(id);
            redirectAttributes.addAttribute("sucesso", "Usuário desativado com sucesso");
            return "redirect:/admin/usuarios";
        } catch (Exception e) {
            log.error("Erro ao desativar usuário", e);
            redirectAttributes.addAttribute("erro", "Erro ao desativar usuário: " + e.getMessage());
            return "redirect:/admin/usuarios";
        }
    }
}
