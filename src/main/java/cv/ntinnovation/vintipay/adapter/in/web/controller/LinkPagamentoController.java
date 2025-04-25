package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.LinkPagamentoRequest;
import cv.ntinnovation.vintipay.adapter.in.web.dto.LinkPagamentoResponse;
import cv.ntinnovation.vintipay.adapter.in.web.mapper.LinkPagamentoWebMapper;
import cv.ntinnovation.vintipay.application.core.domain.entity.LinkPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Produto;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusLinkPagamentoEnum;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/links-pagamento")
@RequiredArgsConstructor
public class LinkPagamentoController {

    private final GerenciaLinkPagamentoUseCase gerenciaLinkPagamentoUseCase;
    private final GerenciaProdutoUseCase gerenciaProdutoUseCase;
    private final ProcessamentoPagamentoLinkUseCase processamentoPagamentoLinkUseCase;
    private final LinkPagamentoWebMapper linkPagamentoWebMapper;

    // ===== Endpoints para interface web =====

    @GetMapping
    public String listarLinks(Model model, Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        List<LinkPagamento> links = gerenciaLinkPagamentoUseCase.listarLinksPorProprietario(usuarioId);
        model.addAttribute("links", links);
        return "links/lista";
    }

    @GetMapping("/novo")
    public String formularioNovoLink(Model model, Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        
        // Buscar produtos do usuário para o dropdown
        List<Produto> produtos = gerenciaProdutoUseCase.listarProdutosPorProprietario(usuarioId);
        
        model.addAttribute("link", new LinkPagamentoRequest());
        model.addAttribute("produtos", produtos);
        
        return "links/form";
    }

    @PostMapping("/novo")
    public String criarLink(@Valid @ModelAttribute("link") LinkPagamentoRequest request, 
                           Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        
        // Verificar se o produto pertence ao usuário
        if (!gerenciaProdutoUseCase.verificarPropriedadeProduto(request.getProdutoId(), usuarioId)) {
            return "redirect:/links-pagamento";
        }
        
        gerenciaLinkPagamentoUseCase.criarLinkPagamento(
            request.getProdutoId(),
            usuarioId,
            request.getTitulo(),
            request.getDescricao(),
            request.getDataExpiracao(),
            request.getQuantidadeUsos(),
            request.getUrlRetornoSucesso(),
            request.getUrlRetornoCancelamento()
        );
        
        return "redirect:/links-pagamento";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditarLink(@PathVariable Long id, Model model, Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        
        Optional<LinkPagamento> linkOpt = gerenciaLinkPagamentoUseCase.buscarLinkPorId(id);
        if (linkOpt.isEmpty() || !gerenciaLinkPagamentoUseCase.verificarPropriedadeLink(id, usuarioId)) {
            return "redirect:/links-pagamento";
        }
        
        LinkPagamento link = linkOpt.get();
        
        // Buscar produtos do usuário para o dropdown
        List<Produto> produtos = gerenciaProdutoUseCase.listarProdutosPorProprietario(usuarioId);
        
        model.addAttribute("link", linkPagamentoWebMapper.toLinkPagamentoRequest(link));
        model.addAttribute("produtos", produtos);
        model.addAttribute("linkId", id);
        
        return "links/form";
    }

    @PostMapping("/{id}/editar")
    public String atualizarLink(@PathVariable Long id, 
                               @Valid @ModelAttribute("link") LinkPagamentoRequest request,
                               Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        
        if (!gerenciaLinkPagamentoUseCase.verificarPropriedadeLink(id, usuarioId)) {
            return "redirect:/links-pagamento";
        }
        
        gerenciaLinkPagamentoUseCase.atualizarLinkPagamento(
            id,
            request.getTitulo(),
            request.getDescricao(),
            request.getDataExpiracao(),
            request.getQuantidadeUsos(),
            request.getStatus(),
            request.getUrlRetornoSucesso(),
            request.getUrlRetornoCancelamento()
        );
        
        return "redirect:/links-pagamento";
    }

    @PostMapping("/{id}/excluir")
    public String excluirLink(@PathVariable Long id, Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        
        if (gerenciaLinkPagamentoUseCase.verificarPropriedadeLink(id, usuarioId)) {
            gerenciaLinkPagamentoUseCase.excluirLinkPagamento(id);
        }
        
        return "redirect:/links-pagamento";
    }

    // ===== Endpoints públicos para checkout =====

    @GetMapping("/p/{codigo}")
    public String paginaCheckout(@PathVariable String codigo, Model model) {
        Optional<LinkPagamento> linkOpt = gerenciaLinkPagamentoUseCase.buscarLinkPorCodigo(codigo);
        
        if (linkOpt.isEmpty() || !linkOpt.get().isDisponivel()) {
            model.addAttribute("erro", "Link de pagamento inválido ou expirado");
            return "links/erro";
        }
        
        LinkPagamento link = linkOpt.get();
        Produto produto = link.getProduto();
        
        model.addAttribute("link", link);
        model.addAttribute("produto", produto);
        model.addAttribute("codigo", codigo);
        
        return "links/checkout";
    }

    @PostMapping("/p/{codigo}/pagar")
    public String processarPagamento(@PathVariable String codigo, HttpServletRequest request) {
        Optional<LinkPagamento> linkOpt = gerenciaLinkPagamentoUseCase.buscarLinkPorCodigo(codigo);
        
        if (linkOpt.isEmpty() || !linkOpt.get().isDisponivel()) {
            return "redirect:/links-pagamento/p/" + codigo + "?erro=link_invalido";
        }
        
        LinkPagamento link = linkOpt.get();
        
        // Construir URLs de sucesso e cancelamento
        String baseUrl = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            baseUrl += ":" + request.getServerPort();
        }
        
        String successUrl = link.getUrlRetornoSucesso();
        if (successUrl == null || successUrl.isEmpty()) {
            successUrl = baseUrl + "/links-pagamento/p/" + codigo + "/sucesso";
        }
        
        String cancelUrl = link.getUrlRetornoCancelamento();
        if (cancelUrl == null || cancelUrl.isEmpty()) {
            cancelUrl = baseUrl + "/links-pagamento/p/" + codigo + "/cancelado";
        }
        
        // Criar sessão de pagamento
        String checkoutUrl = processamentoPagamentoLinkUseCase.criarSessaoPagamento(codigo, successUrl, cancelUrl);
        
        return "redirect:" + checkoutUrl;
    }

    @GetMapping("/p/{codigo}/sucesso")
    public String paginaSucesso(@PathVariable String codigo, 
                               @RequestParam String sessionId,
                               Model model) {
        Optional<LinkPagamento> linkOpt = gerenciaLinkPagamentoUseCase.buscarLinkPorCodigo(codigo);
        
        if (linkOpt.isEmpty()) {
            model.addAttribute("erro", "Link de pagamento não encontrado");
            return "links/erro";
        }
        
        LinkPagamento link = linkOpt.get();
        Produto produto = link.getProduto();
        
        // Processar o pagamento bem-sucedido
        boolean sucesso = processamentoPagamentoLinkUseCase.processarPagamentoSucesso(sessionId, codigo);
        
        if (!sucesso) {
            model.addAttribute("erro", "Não foi possível confirmar o pagamento");
            return "links/erro";
        }
        
        model.addAttribute("link", link);
        model.addAttribute("produto", produto);
        
        return "links/sucesso";
    }

    @GetMapping("/p/{codigo}/cancelado")
    public String paginaCancelamento(@PathVariable String codigo, Model model) {
        Optional<LinkPagamento> linkOpt = gerenciaLinkPagamentoUseCase.buscarLinkPorCodigo(codigo);
        
        if (linkOpt.isEmpty()) {
            model.addAttribute("erro", "Link de pagamento não encontrado");
            return "links/erro";
        }
        
        LinkPagamento link = linkOpt.get();
        Produto produto = link.getProduto();
        
        model.addAttribute("link", link);
        model.addAttribute("produto", produto);
        
        return "links/cancelado";
    }

    // ===== API REST =====

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<LinkPagamentoResponse>> listarLinksApi(Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        List<LinkPagamento> links = gerenciaLinkPagamentoUseCase.listarLinksPorProprietario(usuarioId);
        
        List<LinkPagamentoResponse> response = links.stream()
                .map(linkPagamentoWebMapper::toLinkPagamentoResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<LinkPagamentoResponse> buscarLinkApi(@PathVariable Long id, Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        
        Optional<LinkPagamento> linkOpt = gerenciaLinkPagamentoUseCase.buscarLinkPorId(id);
        if (linkOpt.isEmpty() || !gerenciaLinkPagamentoUseCase.verificarPropriedadeLink(id, usuarioId)) {
            return ResponseEntity.notFound().build();
        }
        
        LinkPagamentoResponse response = linkPagamentoWebMapper.toLinkPagamentoResponse(linkOpt.get());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<LinkPagamentoResponse> criarLinkApi(@Valid @RequestBody LinkPagamentoRequest request, 
                                                            Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        
        // Verificar se o produto pertence ao usuário
        if (!gerenciaProdutoUseCase.verificarPropriedadeProduto(request.getProdutoId(), usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        LinkPagamento link = gerenciaLinkPagamentoUseCase.criarLinkPagamento(
            request.getProdutoId(),
            usuarioId,
            request.getTitulo(),
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
        Long usuarioId = Long.parseLong(authentication.getName());
        
        if (!gerenciaLinkPagamentoUseCase.verificarPropriedadeLink(id, usuarioId)) {
            return ResponseEntity.notFound().build();
        }
        
        LinkPagamento link = gerenciaLinkPagamentoUseCase.atualizarLinkPagamento(
            id,
            request.getTitulo(),
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
        Long usuarioId = Long.parseLong(authentication.getName());
        
        if (!gerenciaLinkPagamentoUseCase.verificarPropriedadeLink(id, usuarioId)) {
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
        Long usuarioId = Long.parseLong(authentication.getName());
        
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
