package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.ProdutoRequest;
import cv.ntinnovation.vintipay.adapter.in.web.dto.ProdutoResponse;
import cv.ntinnovation.vintipay.adapter.in.web.mapper.ProdutoWebMapper;
import cv.ntinnovation.vintipay.application.core.domain.entity.Produto;
import cv.ntinnovation.vintipay.application.port.in.GerenciaProdutoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final GerenciaProdutoUseCase gerenciaProdutoUseCase;
    private final ProdutoWebMapper produtoWebMapper;

    // ===== Endpoints para interface web =====

    @GetMapping
    public String listarProdutos(Model model, Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        List<Produto> produtos = gerenciaProdutoUseCase.listarProdutosPorProprietario(usuarioId);
        model.addAttribute("produtos", produtos);
        return "produtos/lista";
    }

    @GetMapping("/novo")
    public String formularioNovoProduto(Model model) {
        model.addAttribute("produto", new ProdutoRequest());
        return "produtos/form";
    }

    @PostMapping("/novo")
    public String criarProduto(@Valid @ModelAttribute("produto") ProdutoRequest request, 
                              Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        
        gerenciaProdutoUseCase.criarProduto(
            request.getNome(),
            request.getDescricao(),
            request.getPreco(),
            request.getImagem(),
            request.getCodigoSku(),
            request.getEstoque(),
            request.getCategoria(),
            usuarioId
        );
        
        return "redirect:/produtos";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditarProduto(@PathVariable Long id, Model model, Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        
        Optional<Produto> produtoOpt = gerenciaProdutoUseCase.buscarProdutoPorId(id);
        if (produtoOpt.isEmpty() || !gerenciaProdutoUseCase.verificarPropriedadeProduto(id, usuarioId)) {
            return "redirect:/produtos";
        }
        
        Produto produto = produtoOpt.get();
        model.addAttribute("produto", produtoWebMapper.toProdutoRequest(produto));
        model.addAttribute("produtoId", id);
        
        return "produtos/form";
    }

    @PostMapping("/{id}/editar")
    public String atualizarProduto(@PathVariable Long id, 
                                  @Valid @ModelAttribute("produto") ProdutoRequest request,
                                  Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        
        if (!gerenciaProdutoUseCase.verificarPropriedadeProduto(id, usuarioId)) {
            return "redirect:/produtos";
        }
        
        gerenciaProdutoUseCase.atualizarProduto(
            id,
            request.getNome(),
            request.getDescricao(),
            request.getPreco(),
            request.getImagem(),
            request.getCodigoSku(),
            request.getEstoque(),
            request.getCategoria(),
            request.isAtivo()
        );
        
        return "redirect:/produtos";
    }

    @PostMapping("/{id}/excluir")
    public String excluirProduto(@PathVariable Long id, Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        
        if (gerenciaProdutoUseCase.verificarPropriedadeProduto(id, usuarioId)) {
            gerenciaProdutoUseCase.excluirProduto(id);
        }
        
        return "redirect:/produtos";
    }

    // ===== API REST =====

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<ProdutoResponse>> listarProdutosApi(Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        List<Produto> produtos = gerenciaProdutoUseCase.listarProdutosPorProprietario(usuarioId);
        
        List<ProdutoResponse> response = produtos.stream()
                .map(produtoWebMapper::toProdutoResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<ProdutoResponse> buscarProdutoApi(@PathVariable Long id, Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        
        Optional<Produto> produtoOpt = gerenciaProdutoUseCase.buscarProdutoPorId(id);
        if (produtoOpt.isEmpty() || !gerenciaProdutoUseCase.verificarPropriedadeProduto(id, usuarioId)) {
            return ResponseEntity.notFound().build();
        }
        
        ProdutoResponse response = produtoWebMapper.toProdutoResponse(produtoOpt.get());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<ProdutoResponse> criarProdutoApi(@Valid @RequestBody ProdutoRequest request, 
                                                         Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        
        Produto produto = gerenciaProdutoUseCase.criarProduto(
            request.getNome(),
            request.getDescricao(),
            request.getPreco(),
            request.getImagem(),
            request.getCodigoSku(),
            request.getEstoque(),
            request.getCategoria(),
            usuarioId
        );
        
        ProdutoResponse response = produtoWebMapper.toProdutoResponse(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<ProdutoResponse> atualizarProdutoApi(@PathVariable Long id, 
                                                             @Valid @RequestBody ProdutoRequest request,
                                                             Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        
        if (!gerenciaProdutoUseCase.verificarPropriedadeProduto(id, usuarioId)) {
            return ResponseEntity.notFound().build();
        }
        
        Produto produto = gerenciaProdutoUseCase.atualizarProduto(
            id,
            request.getNome(),
            request.getDescricao(),
            request.getPreco(),
            request.getImagem(),
            request.getCodigoSku(),
            request.getEstoque(),
            request.getCategoria(),
            request.isAtivo()
        );
        
        ProdutoResponse response = produtoWebMapper.toProdutoResponse(produto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> excluirProdutoApi(@PathVariable Long id, Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        
        if (!gerenciaProdutoUseCase.verificarPropriedadeProduto(id, usuarioId)) {
            return ResponseEntity.notFound().build();
        }
        
        boolean sucesso = gerenciaProdutoUseCase.excluirProduto(id);
        
        if (sucesso) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }
}
