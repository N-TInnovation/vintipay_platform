package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.ConfiguracaoPagamentoRequest;
import cv.ntinnovation.vintipay.adapter.in.web.dto.ConfiguracaoPagamentoResponse;
import cv.ntinnovation.vintipay.adapter.in.web.mapper.ConfiguracaoPagamentoWebMapper;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.UsuarioRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.mapper.UsuarioMapper;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.GerenciaConfiguracaoPagamentoUsuarioUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/configuracoes-pagamento")
@RequiredArgsConstructor
public class ConfiguracaoPagamentoUsuarioController {

    private final GerenciaConfiguracaoPagamentoUsuarioUseCase gerenciaConfiguracaoPagamentoUsuarioUseCase;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final ConfiguracaoPagamentoWebMapper configuracaoPagamentoWebMapper;

    @GetMapping
    public String listarConfiguracoes(Model model) {
        Usuario usuario = getUsuarioAutenticado();
        List<ConfiguracaoPagamento> configuracoes = gerenciaConfiguracaoPagamentoUsuarioUseCase.buscarConfiguracoesDoUsuario(usuario.getId());
        model.addAttribute("configuracoes", configuracoes);
        return "configuracoes/lista";
    }

    @GetMapping("/nova")
    public String exibirFormularioNova(Model model) {
        model.addAttribute("configuracao", new ConfiguracaoPagamentoRequest());
        return "configuracoes/form";
    }

    @PostMapping("/nova")
    public String criarConfiguracao(
            @Valid @ModelAttribute("configuracao") ConfiguracaoPagamentoRequest request,
            BindingResult result,
            Model model) {
        
        if (result.hasErrors()) {
            return "configuracoes/form";
        }

        try {
            Usuario usuario = getUsuarioAutenticado();
            ConfiguracaoPagamento configuracao = configuracaoPagamentoWebMapper.toDomain(request);
            gerenciaConfiguracaoPagamentoUsuarioUseCase.criarConfiguracaoParaUsuario(configuracao, usuario.getId());
            return "redirect:/configuracoes-pagamento?sucesso";
        } catch (Exception e) {
            log.error("Erro ao criar configuração de pagamento", e);
            model.addAttribute("erro", "Erro ao criar configuração: " + e.getMessage());
            return "configuracoes/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String exibirFormularioEditar(@PathVariable Long id, Model model) {
        Usuario usuario = getUsuarioAutenticado();
        ConfiguracaoPagamento configuracao = gerenciaConfiguracaoPagamentoUsuarioUseCase
                .buscarConfiguracaoPorIdEUsuario(id, usuario.getId())
                .orElseThrow(() -> new RuntimeException("Configuração não encontrada"));
        
        model.addAttribute("configuracao", configuracaoPagamentoWebMapper.toRequest(configuracao));
        return "configuracoes/form";
    }

    @PostMapping("/{id}/editar")
    public String atualizarConfiguracao(
            @PathVariable Long id,
            @Valid @ModelAttribute("configuracao") ConfiguracaoPagamentoRequest request,
            BindingResult result,
            Model model) {
        
        if (result.hasErrors()) {
            return "configuracoes/form";
        }

        try {
            Usuario usuario = getUsuarioAutenticado();
            ConfiguracaoPagamento configuracao = configuracaoPagamentoWebMapper.toDomain(request);
            gerenciaConfiguracaoPagamentoUsuarioUseCase.atualizarConfiguracaoParaUsuario(id, configuracao, usuario.getId());
            return "redirect:/configuracoes-pagamento?sucesso";
        } catch (Exception e) {
            log.error("Erro ao atualizar configuração de pagamento", e);
            model.addAttribute("erro", "Erro ao atualizar configuração: " + e.getMessage());
            return "configuracoes/form";
        }
    }

    @PostMapping("/{id}/ativar")
    public String ativarConfiguracao(@PathVariable Long id) {
        Usuario usuario = getUsuarioAutenticado();
        gerenciaConfiguracaoPagamentoUsuarioUseCase.ativarConfiguracao(id, usuario.getId());
        return "redirect:/configuracoes-pagamento?sucesso";
    }

    @PostMapping("/{id}/desativar")
    public String desativarConfiguracao(@PathVariable Long id) {
        Usuario usuario = getUsuarioAutenticado();
        gerenciaConfiguracaoPagamentoUsuarioUseCase.desativarConfiguracao(id, usuario.getId());
        return "redirect:/configuracoes-pagamento?sucesso";
    }

    // API REST para integração com outros sistemas

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<ConfiguracaoPagamentoResponse>> listarConfiguracoesApi() {
        Usuario usuario = getUsuarioAutenticado();
        List<ConfiguracaoPagamento> configuracoes = gerenciaConfiguracaoPagamentoUsuarioUseCase.buscarConfiguracoesDoUsuario(usuario.getId());
        List<ConfiguracaoPagamentoResponse> response = configuracoes.stream()
                .map(configuracaoPagamentoWebMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<ConfiguracaoPagamentoResponse> criarConfiguracaoApi(
            @Valid @RequestBody ConfiguracaoPagamentoRequest request) {
        
        Usuario usuario = getUsuarioAutenticado();
        ConfiguracaoPagamento configuracao = configuracaoPagamentoWebMapper.toDomain(request);
        ConfiguracaoPagamento configuracaoSalva = gerenciaConfiguracaoPagamentoUsuarioUseCase.criarConfiguracaoParaUsuario(configuracao, usuario.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(configuracaoPagamentoWebMapper.toResponse(configuracaoSalva));
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<ConfiguracaoPagamentoResponse> atualizarConfiguracaoApi(
            @PathVariable Long id,
            @Valid @RequestBody ConfiguracaoPagamentoRequest request) {
        
        Usuario usuario = getUsuarioAutenticado();
        ConfiguracaoPagamento configuracao = configuracaoPagamentoWebMapper.toDomain(request);
        ConfiguracaoPagamento configuracaoAtualizada = gerenciaConfiguracaoPagamentoUsuarioUseCase.atualizarConfiguracaoParaUsuario(id, configuracao, usuario.getId());
        return ResponseEntity.ok(configuracaoPagamentoWebMapper.toResponse(configuracaoAtualizada));
    }
    
    private Usuario getUsuarioAutenticado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        
        return usuarioRepository.findByUsername(username)
                .map(usuarioMapper::toDomain)
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado: {}", username);
                    return new UsernameNotFoundException("Usuário não encontrado: " + username);
                });
    }
}
