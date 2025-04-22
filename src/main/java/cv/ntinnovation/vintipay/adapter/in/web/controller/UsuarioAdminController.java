package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.out.db.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.adapter.out.db.repository.UsuarioRepository;
import cv.ntinnovation.vintipay.adapter.out.db.mapper.UsusarioMapper;
import cv.ntinnovation.vintipay.application.core.domain.entity.PlanoAssinatura;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.GerenciaAssinaturaUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/admin/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioAdminController {

    private final UsuarioRepository usuarioRepository;
    private final UsusarioMapper usuarioMapper;
    private final GerenciaAssinaturaUseCase gerenciaAssinaturaUseCase;

    @GetMapping
    public String listarUsuarios(Model model) {
        List<UsuarioEntity> usuarios = usuarioRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        return "admin/usuarios/lista";
    }

    @GetMapping("/{id}")
    public String visualizarUsuario(@PathVariable Long id, Model model) {
        Optional<UsuarioEntity> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isEmpty()) {
            return "redirect:/admin/usuarios?erro=Usuário+não+encontrado";
        }
        
        UsuarioEntity usuarioEntity = usuarioOptional.get();
        model.addAttribute("usuario", usuarioEntity);
        
        // Buscar assinatura do usuário, se existir
        Usuario usuario = usuarioMapper.toDomain(usuarioEntity);
        Optional<PlanoAssinatura> assinaturaOptional = gerenciaAssinaturaUseCase.buscarAssinaturaPorUsuario(usuario);
        assinaturaOptional.ifPresent(assinatura -> model.addAttribute("assinatura", assinatura));
        
        return "admin/usuarios/detalhes";
    }

    @PostMapping("/{id}/ativar")
    public String ativarUsuario(@PathVariable Long id) {
        Optional<UsuarioEntity> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isEmpty()) {
            return "redirect:/admin/usuarios?erro=Usuário+não+encontrado";
        }
        
        UsuarioEntity usuario = usuarioOptional.get();
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
        
        return "redirect:/admin/usuarios?sucesso=Usuário+ativado+com+sucesso";
    }

    @PostMapping("/{id}/desativar")
    public String desativarUsuario(@PathVariable Long id) {
        Optional<UsuarioEntity> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isEmpty()) {
            return "redirect:/admin/usuarios?erro=Usuário+não+encontrado";
        }
        
        UsuarioEntity usuario = usuarioOptional.get();
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
        
        return "redirect:/admin/usuarios?sucesso=Usuário+desativado+com+sucesso";
    }
}
