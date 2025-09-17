package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.DescontoCiclo;
import cv.ntinnovation.vintipay.application.core.domain.entity.Produto;
import cv.ntinnovation.vintipay.application.core.domain.enums.CategoriaProdutoEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.PeriodoRecorrenciaEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.PrimeiraCobrancaEnum;

import java.math.BigDecimal;
import java.util.List;

public interface GerenciaProdutoUseCase {
    /**
     * Cria um novo produto
     * 
     * @param nome Nome do produto
     * @param descricao Descrição do produto
     * @param preco Preço do produto
     * @param imagem URL da imagem do produto (opcional)
     * @param codigoSku Código SKU do produto (opcional)
     * @param estoque Quantidade em estoque (opcional)
     * @param categoria Categoria do produto (opcional)
     * @param proprietarioId ID do usuário proprietário do produto
     * @return Produto criado
     */
    Produto criarProduto(String nome, String descricao, BigDecimal preco, 
                        String imagem, String codigoSku, Integer estoque,
                        CategoriaProdutoEnum categoria, Long proprietarioId);
    
    /**
     * Atualiza um produto existente
     * 
     * @param produtoId ID do produto a ser atualizado
     * @param nome Nome do produto
     * @param descricao Descrição do produto
     * @param preco Preço do produto
     * @param imagem URL da imagem do produto
     * @param codigoSku Código SKU do produto
     * @param estoque Quantidade em estoque
     * @param categoria Categoria do produto
     * @param ativo Status de ativação do produto
     * @return Produto atualizado
     */
    Produto atualizarProduto(Long produtoId, String nome, String descricao, 
                           BigDecimal preco, String imagem, String codigoSku,
                           Integer estoque, CategoriaProdutoEnum categoria, boolean ativo);
    
    /**
     * Atualiza um produto existente incluindo configurações de recorrência
     * 
     * @param produtoId ID do produto a ser atualizado
     * @param nome Nome do produto
     * @param descricao Descrição do produto
     * @param preco Preço do produto
     * @param imagem URL da imagem do produto
     * @param codigoSku Código SKU do produto
     * @param estoque Quantidade em estoque
     * @param categoria Categoria do produto
     * @param ativo Status de ativação do produto
     * @param recorrente Indica se o produto é recorrente
     * @param periodoRecorrencia Período de recorrência (mensal, anual, etc.)
     * @param duracaoRecorrencia Número de ciclos (null = ilimitado)
     * @param descricaoRecorrencia Descrição amigável da recorrência
     * @param primeiraCobranca Momento da primeira cobrança (IMEDIATA, APOS_PERIODO)
     * @param descontosPorCiclo Lista de descontos por ciclo
     * @return Produto atualizado
     */
    Produto atualizarProdutoComRecorrencia(Long produtoId, String nome, String descricao, 
                                        BigDecimal preco, String imagem, String codigoSku,
                                        Integer estoque, CategoriaProdutoEnum categoria, boolean ativo,
                                        boolean recorrente, PeriodoRecorrenciaEnum periodoRecorrencia, 
                                        Integer duracaoRecorrencia, String descricaoRecorrencia,
                                        PrimeiraCobrancaEnum primeiraCobranca, List<DescontoCiclo> descontosPorCiclo);
    
    /**
     * Ativa ou desativa um produto
     * 
     * @param produtoId ID do produto
     * @param ativo true para ativar, false para desativar
     * @return true se a operação foi bem-sucedida
     */
    boolean alterarStatusProduto(Long produtoId, boolean ativo);
    
    /**
     * Exclui um produto
     * 
     * @param produtoId ID do produto a ser excluído
     * @return true se a exclusão foi bem-sucedida
     */
    boolean excluirProduto(Long produtoId);
    
    /**
     * Lista todos os produtos de um usuário
     * 
     * @param proprietarioId ID do usuário proprietário
     * @return Lista de produtos
     */
    List<Produto> listarProdutosPorProprietario(Long proprietarioId);
    
    /**
     * Busca um produto pelo ID
     * 
     * @param produtoId ID do produto
     * @return Produto encontrado ou lança exceção se não encontrado
     * @throws RuntimeException se o produto não for encontrado
     */
    Produto buscarProdutoPorId(Long produtoId);
    
    /**
     * Verifica se um produto pertence a um usuário
     * 
     * @param produtoId ID do produto
     * @param proprietarioId ID do usuário
     * @return true se o produto pertence ao usuário
     */
    boolean verificarPropriedadeProduto(Long produtoId, Long proprietarioId);
}
