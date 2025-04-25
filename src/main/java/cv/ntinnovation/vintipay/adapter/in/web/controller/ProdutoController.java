package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.ProdutoRequest;
import cv.ntinnovation.vintipay.adapter.in.web.dto.ProdutoResponse;
import cv.ntinnovation.vintipay.adapter.in.web.mapper.ProdutoWebMapper;
import cv.ntinnovation.vintipay.application.core.domain.entity.Produto;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.GerenciaProdutoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final GerenciaProdutoUseCase gerenciaProdutoUseCase;
    private final ProdutoWebMapper produtoWebMapper;

    // ===== Endpoints para interface web =====

    @GetMapping
    public String listarProdutos(Model model, Authentication authentication) {
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
        List<Produto> produtos = gerenciaProdutoUseCase.listarProdutosPorProprietario(usuarioId);
        model.addAttribute("produtos", produtos);
        model.addAttribute("usuario", usuario);
        return "user/produtos";
    }

    @GetMapping("/novo")
    public String exibirFormularioNovoProduto(Model model) {
        model.addAttribute("produto", new ProdutoRequest());
        return "user/produto-form";
    }

    @PostMapping("/novo")
    public String criarProduto(@Valid @ModelAttribute("produto") ProdutoRequest request, 
                              BindingResult result, 
                              Model model, 
                              Authentication authentication) {
        if (result.hasErrors()) {
            return "user/produto-form";
        }
        
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
        try {
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
            
            return "redirect:/user/produtos";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao criar produto: " + e.getMessage());
            return "user/produto-form";
        }
    }

    @GetMapping("/{id}/editar")
    public String exibirFormularioEditarProduto(@PathVariable Long id, Model model, Authentication authentication) {
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
        try {
            Produto produto = gerenciaProdutoUseCase.buscarProdutoPorId(id);
            
            // Verificar se o produto pertence ao usuário logado
            if (!produto.getProprietarioId().equals(usuarioId)) {
                return "redirect:/user/produtos?erro=Você não tem permissão para editar este produto";
            }
            
            ProdutoRequest produtoRequest = new ProdutoRequest();
            produtoRequest.setNome(produto.getNome());
            produtoRequest.setDescricao(produto.getDescricao());
            produtoRequest.setPreco(produto.getPreco());
            produtoRequest.setImagem(produto.getImagem());
            produtoRequest.setCodigoSku(produto.getCodigoSku());
            produtoRequest.setEstoque(produto.getEstoque());
            produtoRequest.setCategoria(produto.getCategoria());
            produtoRequest.setAtivo(produto.isAtivo());
            
            model.addAttribute("produto", produtoRequest);
            model.addAttribute("produtoId", id);
            
            return "user/produto-form";
        } catch (Exception e) {
            return "redirect:/user/produtos?erro=Produto não encontrado";
        }
    }

    @PostMapping("/{id}/editar")
    public String editarProduto(@PathVariable Long id,
                               @Valid @ModelAttribute("produto") ProdutoRequest request,
                               BindingResult result,
                               Model model,
                               Authentication authentication) {
        if (result.hasErrors()) {
            model.addAttribute("produtoId", id);
            return "user/produto-form";
        }
        
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
        try {
            Produto produto = gerenciaProdutoUseCase.buscarProdutoPorId(id);
            
            // Verificar se o produto pertence ao usuário logado
            if (!produto.getProprietarioId().equals(usuarioId)) {
                return "redirect:/user/produtos?erro=Você não tem permissão para editar este produto";
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
            
            return "redirect:/user/produtos";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao atualizar produto: " + e.getMessage());
            model.addAttribute("produtoId", id);
            return "user/produto-form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluirProduto(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
        try {
            Produto produto = gerenciaProdutoUseCase.buscarProdutoPorId(id);
            
            // Verificar se o produto pertence ao usuário logado
            if (!produto.getProprietarioId().equals(usuarioId)) {
                redirectAttributes.addFlashAttribute("erro", "Você não tem permissão para excluir este produto");
                return "redirect:/user/produtos";
            }
            
            gerenciaProdutoUseCase.excluirProduto(id);
            redirectAttributes.addFlashAttribute("sucesso", "Produto excluído com sucesso");
            return "redirect:/user/produtos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir produto: " + e.getMessage());
            return "redirect:/user/produtos";
        }
    }

    // ===== API REST =====

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<ProdutoResponse>> listarProdutosApi(Authentication authentication) {
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
        List<Produto> produtos = gerenciaProdutoUseCase.listarProdutosPorProprietario(usuarioId);
        
        List<ProdutoResponse> response = produtos.stream()
                .map(produtoWebMapper::toProdutoResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<ProdutoResponse> buscarProdutoApi(@PathVariable Long id, Authentication authentication) {
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
        try {
            Produto produto = gerenciaProdutoUseCase.buscarProdutoPorId(id);
            
            // Verificar se o produto pertence ao usuário logado
            if (!produto.getProprietarioId().equals(usuarioId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            ProdutoResponse response = produtoWebMapper.toProdutoResponse(produto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<ProdutoResponse> criarProdutoApi(@Valid @RequestBody ProdutoRequest request, 
                                                         Authentication authentication) {
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
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
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
        try {
            Produto produto = gerenciaProdutoUseCase.buscarProdutoPorId(id);
            
            // Verificar se o produto pertence ao usuário logado
            if (!produto.getProprietarioId().equals(usuarioId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            Produto produtoAtualizado = gerenciaProdutoUseCase.atualizarProduto(
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
            
            ProdutoResponse response = produtoWebMapper.toProdutoResponse(produtoAtualizado);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> excluirProdutoApi(@PathVariable Long id, Authentication authentication) {
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
        try {
            Produto produto = gerenciaProdutoUseCase.buscarProdutoPorId(id);
            
            // Verificar se o produto pertence ao usuário logado
            if (!produto.getProprietarioId().equals(usuarioId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            boolean sucesso = gerenciaProdutoUseCase.excluirProduto(id);
            
            if (sucesso) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.internalServerError().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
