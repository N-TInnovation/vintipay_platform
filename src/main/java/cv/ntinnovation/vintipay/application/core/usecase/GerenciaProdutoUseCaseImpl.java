package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.DescontoCiclo;
import cv.ntinnovation.vintipay.application.core.domain.entity.Produto;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.core.domain.enums.CategoriaProdutoEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.PeriodoRecorrenciaEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.PrimeiraCobrancaEnum;
import cv.ntinnovation.vintipay.application.port.in.GerenciaProdutoUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaProduto;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuario;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuarioPort;
import cv.ntinnovation.vintipay.application.port.out.SalvaProduto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class GerenciaProdutoUseCaseImpl implements GerenciaProdutoUseCase {

    private final SalvaProduto salvaProduto;
    private final BuscaProduto buscaProduto;
    private final BuscaUsuarioPort buscaUsuarioPort;
    private final BuscaUsuario buscaUsuario;

    @Override
    @Transactional
    public Produto criarProduto(String nome, String descricao, BigDecimal preco, 
                              String imagem, String codigoSku, Integer estoque,
                              CategoriaProdutoEnum categoria, Long proprietarioId) {
        log.info("Criando produto para o usuário: {}", proprietarioId);
        
        // Validar campos obrigatórios
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório");
        }
        
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço do produto deve ser maior que zero");
        }
        
        if (imagem == null || imagem.trim().isEmpty()) {
            throw new IllegalArgumentException("Imagem do produto é obrigatória");
        }
        
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria do produto é obrigatória");
        }
        
        // Buscar o usuário proprietário
        Optional<Usuario> usuarioOpt = buscaUsuario.buscarPorId(proprietarioId);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        
        // Criar o produto
        Produto produto = Produto.builder()
                .nome(nome)
                .descricao(descricao)
                .preco(preco)
                .imagem(imagem)
                .codigoSku(codigoSku)
                .estoque(estoque)
                .categoria(categoria)
                .ativo(true)
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .proprietarioId(proprietarioId)
                .moeda("CVE") // Moeda padrão para Cabo Verde
                .build();
        
        // Salvar o produto
        return salvaProduto.salvar(produto);
    }

    @Override
    @Transactional
    public Produto atualizarProduto(Long produtoId, String nome, String descricao, 
                                   BigDecimal preco, String imagem, String codigoSku,
                                   Integer estoque, CategoriaProdutoEnum categoria, boolean ativo) {
        log.info("Atualizando produto: {}", produtoId);
        
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
        
        // Validar que a imagem é obrigatória
        if (imagem == null || imagem.trim().isEmpty()) {
            throw new IllegalArgumentException("Imagem do produto é obrigatória");
        }
        produto.setImagem(imagem);
        
        if (codigoSku != null) {
            produto.setCodigoSku(codigoSku);
        }
        
        if (estoque != null) {
            produto.setEstoque(estoque);
        }
        
        // Validar que a categoria é obrigatória
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria do produto é obrigatória");
        }
        produto.setCategoria(categoria);
        
        produto.setAtivo(ativo);
        produto.setDataAtualizacao(LocalDateTime.now());
        
        // Salvar o produto atualizado
        return salvaProduto.salvar(produto);
    }

    @Override
    @Transactional
    public Produto atualizarProdutoComRecorrencia(Long produtoId, String nome, String descricao, 
                                               BigDecimal preco, String imagem, String codigoSku,
                                               Integer estoque, CategoriaProdutoEnum categoria, boolean ativo,
                                               boolean recorrente, PeriodoRecorrenciaEnum periodoRecorrencia, 
                                               Integer duracaoRecorrencia, String descricaoRecorrencia,
                                               PrimeiraCobrancaEnum primeiraCobranca, List<DescontoCiclo> descontosPorCiclo) {
        log.info("Atualizando produto com configurações de recorrência: {}", produtoId);
        
        // Buscar o produto
        Optional<Produto> produtoOpt = buscaProduto.buscarPorId(produtoId);
        if (produtoOpt.isEmpty()) {
            throw new IllegalArgumentException("Produto não encontrado");
        }
        
        Produto produto = produtoOpt.get();
        
        // Atualizar os campos básicos
        if (nome != null && !nome.trim().isEmpty()) {
            produto.setNome(nome);
        }
        
        if (descricao != null) {
            produto.setDescricao(descricao);
        }
        
        if (preco != null && preco.compareTo(BigDecimal.ZERO) > 0) {
            produto.setPreco(preco);
        }
        
        if (imagem != null && !imagem.trim().isEmpty()) {
            produto.setImagem(imagem);
        }
        
        produto.setCodigoSku(codigoSku);
        produto.setEstoque(estoque);
        produto.setCategoria(categoria);
        produto.setAtivo(ativo);
        produto.setDataAtualizacao(LocalDateTime.now());
        
        // Atualizar campos de recorrência
        produto.setRecorrente(recorrente);
        produto.setPeriodoRecorrencia(periodoRecorrencia);
        produto.setDuracaoRecorrencia(duracaoRecorrencia);
        produto.setDescricaoRecorrencia(descricaoRecorrencia);
        produto.setPrimeiraCobranca(primeiraCobranca);
        
        // Atualizar descontos por ciclo
        if (produto.getDescontosPorCiclo() != null) {
            produto.getDescontosPorCiclo().clear();
        } else {
            produto.setDescontosPorCiclo(new ArrayList<>());
        }
        
        if (descontosPorCiclo != null && !descontosPorCiclo.isEmpty()) {
            // Definir o produtoId para cada desconto
            descontosPorCiclo.forEach(desconto -> desconto.setProdutoId(produtoId));
            produto.getDescontosPorCiclo().addAll(descontosPorCiclo);
        }
        
        // Salvar o produto atualizado
        return salvaProduto.salvar(produto);
    }

    @Override
    @Transactional
    public boolean alterarStatusProduto(Long produtoId, boolean ativo) {
        log.info("Alterando status do produto: {} para {}", produtoId, ativo ? "ativo" : "inativo");
        
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
        log.info("Excluindo produto: {}", produtoId);
        
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
        log.info("Listando produtos do proprietário: {}", proprietarioId);
        return buscaProduto.buscarPorProprietarioId(proprietarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public Produto buscarProdutoPorId(Long produtoId) {
        log.info("Buscando produto por ID: {}", produtoId);
        return buscaProduto.buscarPorId(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + produtoId));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verificarPropriedadeProduto(Long produtoId, Long proprietarioId) {
        log.info("Verificando propriedade do produto: {}, proprietário: {}", produtoId, proprietarioId);
        try {
            Produto produto = buscarProdutoPorId(produtoId);
            return produto.getProprietarioId().equals(proprietarioId);
        } catch (RuntimeException e) {
            return false;
        }
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
}
