package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.Produto;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.GerenciaProdutoUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaProduto;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuarioPort;
import cv.ntinnovation.vintipay.application.port.out.SalvaProduto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GerenciaProdutoUseCaseImpl implements GerenciaProdutoUseCase {

    private final SalvaProduto salvaProduto;
    private final BuscaProduto buscaProduto;
    private final BuscaUsuarioPort buscaUsuarioPort;

    @Override
    @Transactional
    public Produto criarProduto(String nome, String descricao, BigDecimal preco, 
                              String imagem, String codigoSku, Integer estoque,
                              String categoria, Long proprietarioId) {
        // Validações
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório");
        }
        
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço do produto deve ser maior que zero");
        }
        
        // Buscar o usuário proprietário
        Optional<Usuario> proprietarioOpt = buscaUsuarioPort.buscarPorUsername(proprietarioId.toString());
        if (proprietarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário proprietário não encontrado");
        }
        
        // Criar o produto
        Produto produto = Produto.builder()
                .nome(nome)
                .descricao(descricao)
                .preco(preco)
                .imagem(imagem)
                .codigoSku(codigoSku != null ? codigoSku : UUID.randomUUID().toString())
                .estoque(estoque)
                .categoria(categoria)
                .ativo(true)
                .proprietario(proprietarioOpt.get())
                .proprietarioId(proprietarioId)
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .moeda("CVE") // Moeda padrão para Cabo Verde
                .build();
        
        // Salvar o produto
        return salvaProduto.salvar(produto);
    }

    @Override
    @Transactional
    public Produto atualizarProduto(Long produtoId, String nome, String descricao, 
                                  BigDecimal preco, String imagem, String codigoSku,
                                  Integer estoque, String categoria, boolean ativo) {
        // Buscar o produto
        Optional<Produto> produtoOpt = buscaProduto.buscarPorId(produtoId);
        if (produtoOpt.isEmpty()) {
            throw new IllegalArgumentException("Produto não encontrado");
        }
        
        Produto produto = produtoOpt.get();
        
        // Atualizar os campos
        if (nome != null && !nome.trim().isEmpty()) {
            produto.setNome(nome);
        }
        
        if (descricao != null) {
            produto.setDescricao(descricao);
        }
        
        if (preco != null && preco.compareTo(BigDecimal.ZERO) > 0) {
            produto.setPreco(preco);
        }
        
        if (imagem != null) {
            produto.setImagem(imagem);
        }
        
        if (codigoSku != null) {
            produto.setCodigoSku(codigoSku);
        }
        
        if (estoque != null) {
            produto.setEstoque(estoque);
        }
        
        if (categoria != null) {
            produto.setCategoria(categoria);
        }
        
        produto.setAtivo(ativo);
        produto.setDataAtualizacao(LocalDateTime.now());
        
        // Salvar o produto atualizado
        return salvaProduto.salvar(produto);
    }

    @Override
    @Transactional
    public boolean alterarStatusProduto(Long produtoId, boolean ativo) {
        // Buscar o produto
        Optional<Produto> produtoOpt = buscaProduto.buscarPorId(produtoId);
        if (produtoOpt.isEmpty()) {
            return false;
        }
        
        Produto produto = produtoOpt.get();
        
        // Atualizar o status
        produto.setAtivo(ativo);
        produto.setDataAtualizacao(LocalDateTime.now());
        
        // Salvar o produto atualizado
        salvaProduto.salvar(produto);
        
        return true;
    }

    @Override
    @Transactional
    public boolean excluirProduto(Long produtoId) {
        // Buscar o produto
        Optional<Produto> produtoOpt = buscaProduto.buscarPorId(produtoId);
        if (produtoOpt.isEmpty()) {
            return false;
        }
        
        Produto produto = produtoOpt.get();
        
        // Marcar como inativo (soft delete)
        produto.setAtivo(false);
        produto.setDataAtualizacao(LocalDateTime.now());
        
        // Salvar o produto atualizado
        salvaProduto.salvar(produto);
        
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Produto> listarProdutosPorProprietario(Long proprietarioId) {
        return buscaProduto.buscarPorProprietarioId(proprietarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Produto> buscarProdutoPorId(Long produtoId) {
        return buscaProduto.buscarPorId(produtoId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verificarPropriedadeProduto(Long produtoId, Long proprietarioId) {
        return buscaProduto.existePorIdEProprietarioId(produtoId, proprietarioId);
    }
}
