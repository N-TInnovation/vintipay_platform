package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user/perfil")
@RequiredArgsConstructor
public class UserPerfilController {

    @GetMapping({"", "/"})
    public String perfilPage(Model model) {
        // Obter o usuário autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioAtual = (Usuario) authentication.getPrincipal();
        
        // Adicionar dados do usuário ao modelo
        model.addAttribute("usuario", usuarioAtual);
        
        return "user/perfil";
    }
    
    @PostMapping("/atualizar")
    public String atualizarPerfil(
            @RequestParam("nome") String nome,
            @RequestParam("telefone") String telefone,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Obter o usuário autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuarioAtual = (Usuario) authentication.getPrincipal();
            
            // Atualizar dados do usuário (simulação)
            usuarioAtual.setNome(nome);
            usuarioAtual.setTelefone(telefone);
            
            // Em um cenário real, aqui chamaríamos um serviço para persistir as alterações
            // usuarioService.atualizarUsuario(usuarioAtual);
            
            // Adicionar mensagem de sucesso
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Perfil atualizado com sucesso!");
        } catch (Exception e) {
            // Adicionar mensagem de erro
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao atualizar perfil: " + e.getMessage());
        }
        
        return "redirect:/user/perfil";
    }
    
    @PostMapping("/alterar-senha")
    public String alterarSenha(
            @RequestParam("senhaAtual") String senhaAtual,
            @RequestParam("novaSenha") String novaSenha,
            @RequestParam("confirmarSenha") String confirmarSenha,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Verificar se as senhas coincidem
            if (!novaSenha.equals(confirmarSenha)) {
                redirectAttributes.addFlashAttribute("mensagemErroSenha", "As senhas não coincidem.");
                return "redirect:/user/perfil";
            }
            
            // Em um cenário real, aqui verificaríamos a senha atual e atualizaríamos a nova senha
            // usuarioService.alterarSenha(senhaAtual, novaSenha);
            
            // Adicionar mensagem de sucesso
            redirectAttributes.addFlashAttribute("mensagemSucessoSenha", "Senha alterada com sucesso!");
        } catch (Exception e) {
            // Adicionar mensagem de erro
            redirectAttributes.addFlashAttribute("mensagemErroSenha", "Erro ao alterar senha: " + e.getMessage());
        }
        
        return "redirect:/user/perfil";
    }
}
