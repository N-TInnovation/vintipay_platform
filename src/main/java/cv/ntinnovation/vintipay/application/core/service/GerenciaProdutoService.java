package cv.ntinnovation.vintipay.application.core.service;

import cv.ntinnovation.vintipay.application.core.domain.entity.Produto;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.GerenciaProdutoUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaProduto;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuario;
import cv.ntinnovation.vintipay.application.port.out.SalvaProduto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GerenciaProdutoService implements GerenciaProdutoUseCase {

    private final SalvaProduto salvaProduto;
    private final BuscaProduto buscaProduto;
    private final BuscaUsuario buscaUsuario;

    @Override
    @Transactional
    public Produto criarProduto(String nome, String descricao, BigDecimal preco, String imagem, 
                               String codigoSku, Integer estoque, String categoria, Long proprietarioId) {
        log.info("Criando produto: {}, proprietário: {}", nome, proprietarioId);
        
        Usuario proprietario = buscaUsuario.buscarPorId(proprietarioId)
                .orElseThrow(() -> new IllegalArgumentException("Proprietário não encontrado"));
        
        Produto produto = Produto.builder()
                .nome(nome)
                .descricao(descricao)
                .preco(preco)
                .imagem(imagem)
                .codigoSku(codigoSku)
                .estoque(estoque)
                .categoria(categoria)
                .proprietario(proprietario)
                .ativo(true)
                .dataCriacao(LocalDateTime.now())
                .build();
        
        return salvaProduto.salvar(produto);
    }

    @Override
    @Transactional
    public Produto atualizarProduto(Long produtoId, String nome, String descricao, BigDecimal preco, 
                                   String imagem, String codigoSku, Integer estoque, String categoria, 
                                   boolean ativo) {
        log.info("Atualizando produto: {}", produtoId);
        
        Produto produto = buscaProduto.buscarPorId(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
        
        produto.setNome(nome);
        produto.setDescricao(descricao);
        produto.setPreco(preco);
        produto.setImagem(imagem);
        produto.setCodigoSku(codigoSku);
        produto.setEstoque(estoque);
        produto.setCategoria(categoria);
        produto.setAtivo(ativo);
        
        return salvaProduto.salvar(produto);
    }

    @Override
    @Transactional
    public boolean alterarStatusProduto(Long produtoId, boolean ativo) {
        log.info("Alterando status do produto: {} para {}", produtoId, ativo ? "ativo" : "inativo");
        
        Optional<Produto> produtoOpt = buscaProduto.buscarPorId(produtoId);
        if (produtoOpt.isEmpty()) {
            return false;
        }
        
        Produto produto = produtoOpt.get();
        produto.setAtivo(ativo);
        salvaProduto.salvar(produto);
        
        return true;
    }

    @Override
    @Transactional
    public boolean excluirProduto(Long produtoId) {
        log.info("Excluindo produto: {}", produtoId);
        
        Optional<Produto> produtoOpt = buscaProduto.buscarPorId(produtoId);
        if (produtoOpt.isEmpty()) {
            return false;
        }
        
        Produto produto = produtoOpt.get();
        
        // Desativa o produto ao invés de excluí-lo fisicamente
        produto.setAtivo(false);
        salvaProduto.salvar(produto);
        
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Produto> buscarProdutoPorId(Long produtoId) {
        log.info("Buscando produto por ID: {}", produtoId);
        return buscaProduto.buscarPorId(produtoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Produto> listarProdutosPorProprietario(Long proprietarioId) {
        log.info("Listando produtos do proprietário: {}", proprietarioId);
        return buscaProduto.buscarPorProprietarioId(proprietarioId);
    }
    
    /**
     * Lista todos os produtos ativos
     * 
     * @return Lista de produtos ativos
     */
    @Transactional(readOnly = true)
    public List<Produto> listarProdutosAtivos() {
        log.info("Listando todos os produtos ativos");
        return buscaProduto.buscarTodosAtivos();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean verificarPropriedadeProduto(Long produtoId, Long proprietarioId) {
        log.info("Verificando propriedade do produto: {}, proprietário: {}", produtoId, proprietarioId);
        
        Optional<Produto> produtoOpt = buscaProduto.buscarPorId(produtoId);
        if (produtoOpt.isEmpty()) {
            return false;
        }
        
        Produto produto = produtoOpt.get();
        return produto.getProprietario().getId().equals(proprietarioId);
    }
}
