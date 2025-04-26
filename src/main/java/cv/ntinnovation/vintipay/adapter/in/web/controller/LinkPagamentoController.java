package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.LinkPagamentoRequest;
import cv.ntinnovation.vintipay.adapter.in.web.dto.LinkPagamentoResponse;
import cv.ntinnovation.vintipay.adapter.in.web.mapper.LinkPagamentoWebMapper;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.LinkPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Produto;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusLinkPagamentoEnum;
import cv.ntinnovation.vintipay.application.port.in.GerenciaConfiguracaoPagamentoUsuarioUseCase;
import cv.ntinnovation.vintipay.application.port.in.GerenciaLinkPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.GerenciaProdutoUseCase;
import cv.ntinnovation.vintipay.application.port.in.ProcessamentoPagamentoLinkUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user/links-pagamento")
@RequiredArgsConstructor
public class LinkPagamentoController {

    private final GerenciaLinkPagamentoUseCase gerenciaLinkPagamentoUseCase;
    private final GerenciaProdutoUseCase gerenciaProdutoUseCase;
    private final ProcessamentoPagamentoLinkUseCase processamentoPagamentoLinkUseCase;
    private final LinkPagamentoWebMapper linkPagamentoWebMapper;
    private final GerenciaConfiguracaoPagamentoUsuarioUseCase gerenciaConfiguracaoPagamentoUsuarioUseCase;

    // ===== Endpoints para interface web =====

    @GetMapping
    public String listarLinks(Model model, Authentication authentication) {
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
        List<LinkPagamento> links = gerenciaLinkPagamentoUseCase.listarLinksPorProprietario(usuarioId);
        model.addAttribute("links", links);
        return "user/links-pagamento";
    }

    @GetMapping("/novo")
    public String formularioNovoLink(@RequestParam(required = false) Long produtoId, Model model, Authentication authentication) {
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
        // Buscar produtos do usuário para o dropdown
        List<Produto> produtos = gerenciaProdutoUseCase.listarProdutosPorProprietario(usuarioId);
        model.addAttribute("produtos", produtos);
        
        // Buscar configurações de pagamento ativas do usuário
        List<ConfiguracaoPagamento> configuracoes = gerenciaConfiguracaoPagamentoUsuarioUseCase.buscarConfiguracoesAtivasDoUsuario(usuarioId);
        model.addAttribute("configuracoes", configuracoes);
        
        // Inicializar o formulário
        LinkPagamentoRequest request = new LinkPagamentoRequest();
        
        // Se um produtoId foi fornecido, pré-selecionar o produto
        if (produtoId != null) {
            // Verificar se o produto existe e pertence ao usuário
            if (gerenciaProdutoUseCase.verificarPropriedadeProduto(produtoId, usuarioId)) {
                request.setProdutoId(produtoId);
                
                // Buscar o produto para preencher o valor padrão
                try {
                    Produto produto = gerenciaProdutoUseCase.buscarProdutoPorId(produtoId);
                    request.setValor(produto.getPreco());
                    request.setNome("Link para " + produto.getNome());
                } catch (Exception e) {
                    // Ignorar erro e continuar sem pré-preencher
                }
            }
        }
        
        // Se houver apenas uma configuração de pagamento, pré-selecioná-la
        if (configuracoes.size() == 1) {
            request.setConfiguracaoPagamentoId(configuracoes.get(0).getId());
        }
        
        model.addAttribute("link", request);
        
        return "user/link-pagamento-form";
    }

    @PostMapping("/novo")
    public String criarLink(@Valid @ModelAttribute("link") LinkPagamentoRequest request, 
                           BindingResult result,
                           Model model,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
        if (result.hasErrors()) {
            // Buscar produtos do usuário para o dropdown
            List<Produto> produtos = gerenciaProdutoUseCase.listarProdutosPorProprietario(usuarioId);
            model.addAttribute("produtos", produtos);
            
            // Buscar configurações de pagamento ativas do usuário
            List<ConfiguracaoPagamento> configuracoes = gerenciaConfiguracaoPagamentoUsuarioUseCase.buscarConfiguracoesAtivasDoUsuario(usuarioId);
            model.addAttribute("configuracoes", configuracoes);
            
            return "user/link-pagamento-form";
        }
        
        try {
            // Verificar se o produto pertence ao usuário
            if (!gerenciaProdutoUseCase.verificarPropriedadeProduto(request.getProdutoId(), usuarioId)) {
                redirectAttributes.addFlashAttribute("erro", "Produto não encontrado ou não pertence ao usuário");
                return "redirect:/user/links-pagamento";
            }
            
            // Verificar se a configuração de pagamento pertence ao usuário
            boolean configuracaoValida = gerenciaConfiguracaoPagamentoUsuarioUseCase.buscarConfiguracoesDoUsuario(usuarioId)
                .stream()
                .anyMatch(config -> config.getId().equals(request.getConfiguracaoPagamentoId()));
            
            if (!configuracaoValida) {
                redirectAttributes.addFlashAttribute("erro", "Configuração de pagamento não encontrada ou não pertence ao usuário");
                return "redirect:/user/links-pagamento";
            }
            
            // Criar o link de pagamento
            LinkPagamento linkPagamento = gerenciaLinkPagamentoUseCase.criarLinkPagamento(
                request.getProdutoId(),
                usuarioId,
                request.getNome(),
                request.getDescricao(),
                request.getDataExpiracao(),
                request.getQuantidadeUsos(),
                request.getUrlRetornoSucesso(),
                request.getUrlRetornoCancelamento()
            );
            
            // Associar a configuração de pagamento
            linkPagamento = gerenciaLinkPagamentoUseCase.associarConfiguracaoPagamento(
                linkPagamento.getId(),
                request.getConfiguracaoPagamentoId()
            );
            
            redirectAttributes.addFlashAttribute("sucesso", "Link de pagamento criado com sucesso!");
            return "redirect:/user/links-pagamento";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao criar link de pagamento: " + e.getMessage());
            return "redirect:/user/links-pagamento";
        }
    }

    @GetMapping("/{id}/editar")
    public String formularioEditarLink(@PathVariable Long id, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
        try {
            Optional<LinkPagamento> linkOpt = gerenciaLinkPagamentoUseCase.buscarLinkPorId(id);
            if (linkOpt.isEmpty() || !gerenciaLinkPagamentoUseCase.verificarPropriedadeLinkPagamento(id, usuarioId)) {
                redirectAttributes.addFlashAttribute("erro", "Link de pagamento não encontrado ou você não tem permissão para editá-lo");
                return "redirect:/user/links-pagamento";
            }
            
            LinkPagamento link = linkOpt.get();
            
            // Converter para o request
            LinkPagamentoRequest request = linkPagamentoWebMapper.toLinkPagamentoRequest(link);
            
            // Buscar produtos do usuário para o dropdown
            List<Produto> produtos = gerenciaProdutoUseCase.listarProdutosPorProprietario(usuarioId);
            
            model.addAttribute("link", request);
            model.addAttribute("produtos", produtos);
            model.addAttribute("linkId", id);
            
            return "user/link-pagamento-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao carregar link de pagamento: " + e.getMessage());
            return "redirect:/user/links-pagamento";
        }
    }

    @PostMapping("/{id}/editar")
    public String atualizarLink(@PathVariable Long id, 
                               @Valid @ModelAttribute("link") LinkPagamentoRequest request,
                               BindingResult result,
                               Model model,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
        if (result.hasErrors()) {
            // Buscar produtos do usuário para o dropdown
            List<Produto> produtos = gerenciaProdutoUseCase.listarProdutosPorProprietario(usuarioId);
            model.addAttribute("produtos", produtos);
            model.addAttribute("linkId", id);
            return "user/link-pagamento-form";
        }
        
        if (!gerenciaLinkPagamentoUseCase.verificarPropriedadeLinkPagamento(id, usuarioId)) {
            redirectAttributes.addFlashAttribute("erro", "Você não tem permissão para editar este link");
            return "redirect:/user/links-pagamento";
        }
        
        try {
            gerenciaLinkPagamentoUseCase.atualizarLinkPagamento(
                id,
                request.getNome(),
                request.getDescricao(),
                request.getDataExpiracao(),
                request.getQuantidadeUsos(),
                request.getStatus(),
                request.getUrlRetornoSucesso(),
                request.getUrlRetornoCancelamento()
            );
            
            redirectAttributes.addFlashAttribute("sucesso", "Link de pagamento atualizado com sucesso");
            return "redirect:/user/links-pagamento";
        } catch (Exception e) {
            // Buscar produtos do usuário para o dropdown
            List<Produto> produtos = gerenciaProdutoUseCase.listarProdutosPorProprietario(usuarioId);
            model.addAttribute("produtos", produtos);
            model.addAttribute("linkId", id);
            model.addAttribute("erro", "Erro ao atualizar link de pagamento: " + e.getMessage());
            return "user/link-pagamento-form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluirLink(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
        try {
            if (!gerenciaLinkPagamentoUseCase.verificarPropriedadeLinkPagamento(id, usuarioId)) {
                redirectAttributes.addFlashAttribute("erro", "Você não tem permissão para excluir este link");
                return "redirect:/user/links-pagamento";
            }
            
            gerenciaLinkPagamentoUseCase.excluirLinkPagamento(id);
            redirectAttributes.addFlashAttribute("sucesso", "Link de pagamento excluído com sucesso");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir link de pagamento: " + e.getMessage());
        }
        
        return "redirect:/user/links-pagamento";
    }

    // ===== Endpoints públicos para checkout =====
    // Os endpoints públicos foram movidos para PagamentoPublicoController
    // para permitir URLs mais amigáveis como /p/{codigo}

    // ===== API REST =====

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<LinkPagamentoResponse>> listarLinksApi(Authentication authentication) {
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
        List<LinkPagamento> links = gerenciaLinkPagamentoUseCase.listarLinksPorProprietario(usuarioId);
        
        List<LinkPagamentoResponse> response = links.stream()
                .map(linkPagamentoWebMapper::toLinkPagamentoResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<LinkPagamentoResponse> buscarLinkApi(@PathVariable Long id, Authentication authentication) {
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
        Optional<LinkPagamento> linkOpt = gerenciaLinkPagamentoUseCase.buscarLinkPorId(id);
        if (linkOpt.isEmpty() || !gerenciaLinkPagamentoUseCase.verificarPropriedadeLinkPagamento(id, usuarioId)) {
            return ResponseEntity.notFound().build();
        }
        
        LinkPagamentoResponse response = linkPagamentoWebMapper.toLinkPagamentoResponse(linkOpt.get());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<LinkPagamentoResponse> criarLinkApi(@Valid @RequestBody LinkPagamentoRequest request, 
                                                            Authentication authentication) {
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
        // Verificar se o produto pertence ao usuário
        if (!gerenciaProdutoUseCase.verificarPropriedadeProduto(request.getProdutoId(), usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        LinkPagamento link = gerenciaLinkPagamentoUseCase.criarLinkPagamento(
            request.getProdutoId(),
            usuarioId,
            request.getNome(),
            request.getDescricao(),
            request.getDataExpiracao(),
            request.getQuantidadeUsos(),
            request.getUrlRetornoSucesso(),
            request.getUrlRetornoCancelamento()
        );
        
        LinkPagamentoResponse response = linkPagamentoWebMapper.toLinkPagamentoResponse(link);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<LinkPagamentoResponse> atualizarLinkApi(@PathVariable Long id, 
                                                                @Valid @RequestBody LinkPagamentoRequest request,
                                                                Authentication authentication) {
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
        if (!gerenciaLinkPagamentoUseCase.verificarPropriedadeLinkPagamento(id, usuarioId)) {
            return ResponseEntity.notFound().build();
        }
        
        LinkPagamento link = gerenciaLinkPagamentoUseCase.atualizarLinkPagamento(
            id,
            request.getNome(),
            request.getDescricao(),
            request.getDataExpiracao(),
            request.getQuantidadeUsos(),
            request.getStatus(),
            request.getUrlRetornoSucesso(),
            request.getUrlRetornoCancelamento()
        );
        
        LinkPagamentoResponse response = linkPagamentoWebMapper.toLinkPagamentoResponse(link);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> excluirLinkApi(@PathVariable Long id, Authentication authentication) {
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
        if (!gerenciaLinkPagamentoUseCase.verificarPropriedadeLinkPagamento(id, usuarioId)) {
            return ResponseEntity.notFound().build();
        }
        
        boolean sucesso = gerenciaLinkPagamentoUseCase.excluirLinkPagamento(id);
        
        if (sucesso) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/api/produto/{produtoId}")
    @ResponseBody
    public ResponseEntity<List<LinkPagamentoResponse>> listarLinksPorProdutoApi(@PathVariable Long produtoId,
                                                                              Authentication authentication) {
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
        // Verificar se o produto pertence ao usuário
        if (!gerenciaProdutoUseCase.verificarPropriedadeProduto(produtoId, usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<LinkPagamento> links = gerenciaLinkPagamentoUseCase.listarLinksPorProduto(produtoId);
        
        List<LinkPagamentoResponse> response = links.stream()
                .map(linkPagamentoWebMapper::toLinkPagamentoResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
}
