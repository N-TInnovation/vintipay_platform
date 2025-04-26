package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.Produto;
import cv.ntinnovation.vintipay.application.core.domain.enums.CategoriaProdutoEnum;

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
