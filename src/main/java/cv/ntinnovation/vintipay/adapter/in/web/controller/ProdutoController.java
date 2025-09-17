package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.ProdutoRequest;
import cv.ntinnovation.vintipay.adapter.in.web.mapper.DescontoCicloWebMapper;
import cv.ntinnovation.vintipay.adapter.out.storage.ArmazenamentoService;
import cv.ntinnovation.vintipay.application.core.domain.entity.Produto;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.core.domain.enums.CategoriaProdutoEnum;
import cv.ntinnovation.vintipay.application.port.in.GerenciaProdutoUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/user/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final GerenciaProdutoUseCase gerenciaProdutoUseCase;
    private final ArmazenamentoService armazenamentoService;
    private final DescontoCicloWebMapper descontoCicloWebMapper;
    
    @GetMapping
    public String listarProdutos(Model model, Authentication authentication) {
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
        List<Produto> produtos = gerenciaProdutoUseCase.listarProdutosPorProprietario(usuarioId);
        model.addAttribute("produtos", produtos);
        model.addAttribute("usuario", usuario);
        model.addAttribute("categorias", CategoriaProdutoEnum.values());
        return "user/produtos";
    }
    
    @GetMapping("/novo")
    public String exibirFormularioNovoProduto(Model model) {
        model.addAttribute("produto", new ProdutoRequest());
        model.addAttribute("categorias", CategoriaProdutoEnum.values());
        return "user/produto-form";
    }

    @PostMapping("/novo")
    public String criarProduto(@Valid @ModelAttribute("produto") ProdutoRequest request, 
                               BindingResult result, 
                               Model model, 
                               Authentication authentication) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", CategoriaProdutoEnum.values());
            return "user/produto-form";
        }
        
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
        try {
            // Processar o upload da imagem, se houver
            processarUploadImagem(request);
            
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
            log.error("Erro ao criar produto", e);
            model.addAttribute("erro", "Erro ao criar produto: " + e.getMessage());
            model.addAttribute("categorias", CategoriaProdutoEnum.values());
            return "user/produto-form";
        }
    }
    
    @GetMapping("/{id}/editar")
    public String exibirFormularioEdicaoProduto(@PathVariable Long id, Model model, Authentication authentication) {
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
            
            // Copiar informações de recorrência
            produtoRequest.setRecorrente(produto.isRecorrente());
            produtoRequest.setPeriodoRecorrencia(produto.getPeriodoRecorrencia());
            produtoRequest.setDuracaoRecorrencia(produto.getDuracaoRecorrencia());
            produtoRequest.setDescricaoRecorrencia(produto.getDescricaoRecorrencia());
            produtoRequest.setPrimeiraCobranca(produto.getPrimeiraCobranca());
            
            // Copiar descontos por ciclo, se existirem
            if (produto.getDescontosPorCiclo() != null && !produto.getDescontosPorCiclo().isEmpty()) {
                produtoRequest.setDescontosPorCiclo(
                    descontoCicloWebMapper.toRequestList(produto.getDescontosPorCiclo())
                );
            }
            
            model.addAttribute("produto", produtoRequest);
            model.addAttribute("produtoId", id);
            model.addAttribute("categorias", CategoriaProdutoEnum.values());
            
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
            model.addAttribute("categorias", CategoriaProdutoEnum.values());
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
            
            // Processar o upload da imagem, se houver
            processarUploadImagem(request);
            
            // Chamar o novo método que suporta recorrência
            gerenciaProdutoUseCase.atualizarProdutoComRecorrencia(
                id,
                request.getNome(),
                request.getDescricao(),
                request.getPreco(),
                request.getImagem(),
                request.getCodigoSku(),
                request.getEstoque(),
                request.getCategoria(),
                request.isAtivo(),
                request.isRecorrente(),
                request.getPeriodoRecorrencia(),
                request.getDuracaoRecorrencia(),
                request.getDescricaoRecorrencia(),
                request.getPrimeiraCobranca(),
                request.getDescontosPorCiclo() != null ? 
                    request.getDescontosPorCiclo().stream()
                        .map(descontoCicloWebMapper::toDomain)
                        .collect(java.util.stream.Collectors.toList()) : 
                    null
            );
            
            return "redirect:/user/produtos";
        } catch (Exception e) {
            log.error("Erro ao atualizar produto", e);
            model.addAttribute("erro", "Erro ao atualizar produto: " + e.getMessage());
            model.addAttribute("produtoId", id);
            model.addAttribute("categorias", CategoriaProdutoEnum.values());
            return "user/produto-form";
        }
    }
    
    @PostMapping("/{id}/excluir")
    public String excluirProduto(@PathVariable Long id, Authentication authentication) {
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
        try {
            Produto produto = gerenciaProdutoUseCase.buscarProdutoPorId(id);
            
            // Verificar se o produto pertence ao usuário logado
            if (!produto.getProprietarioId().equals(usuarioId)) {
                return "redirect:/user/produtos?erro=Você não tem permissão para excluir este produto";
            }
            
            gerenciaProdutoUseCase.excluirProduto(id);
            return "redirect:/user/produtos";
        } catch (Exception e) {
            return "redirect:/user/produtos?erro=Erro ao excluir produto: " + e.getMessage();
        }
    }
    

    @PostMapping("/{id}/alterarStatus")
    public String alterarStatusProduto(@PathVariable Long id, Authentication authentication) {
        // Obter o objeto Usuario a partir do Authentication
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuario.getId();
        
        try {
            Produto produto = gerenciaProdutoUseCase.buscarProdutoPorId(id);
            
            // Verificar se o produto pertence ao usuário logado
            if (!produto.getProprietarioId().equals(usuarioId)) {
                return "redirect:/user/produtos?erro=Você não tem permissão para alterar este produto";
            }
            
            // Alternar o status
            boolean novoStatus = !produto.isAtivo();
            gerenciaProdutoUseCase.alterarStatusProduto(id, novoStatus);
            
            return "redirect:/user/produtos";
        } catch (Exception e) {
            return "redirect:/user/produtos?erro=Erro ao alterar status do produto: " + e.getMessage();
        }
    }
    
    /**
     * Processa o upload da imagem do produto
     * 
     * @param request Requisição com os dados do produto
     * @throws IOException Se ocorrer um erro ao processar a imagem
     */
    private void processarUploadImagem(ProdutoRequest request) throws IOException {
        MultipartFile imagemFile = request.getImagemFile();
        
        // Se um arquivo foi enviado, processar e armazenar
        if (imagemFile != null && !imagemFile.isEmpty()) {
            // Armazenar o arquivo e obter o caminho
            String caminhoImagem = armazenamentoService.armazenarArquivo(imagemFile, "produtos");
            
            // Atualizar o caminho da imagem no request
            request.setImagem(caminhoImagem);
        }
    }
}
