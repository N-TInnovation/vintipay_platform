package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.LinkPagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusLinkPagamentoEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GerenciaLinkPagamentoUseCase {
    /**
     * Cria um novo link de pagamento para um produto
     * 
     * @param produtoId ID do produto
     * @param proprietarioId ID do usuário proprietário
     * @param titulo Título personalizado para o link (opcional)
     * @param descricao Descrição personalizada (opcional)
     * @param dataExpiracao Data de expiração do link (opcional)
     * @param quantidadeUsos Quantidade máxima de usos permitidos (opcional)
     * @param urlRetornoSucesso URL para redirecionar após pagamento bem-sucedido (opcional)
     * @param urlRetornoCancelamento URL para redirecionar após cancelamento (opcional)
     * @return Link de pagamento criado
     */
    LinkPagamento criarLinkPagamento(Long produtoId, Long proprietarioId, 
                                   String titulo, String descricao,
                                   LocalDateTime dataExpiracao, Integer quantidadeUsos,
                                   String urlRetornoSucesso, String urlRetornoCancelamento);
    
    /**
     * Atualiza um link de pagamento existente
     * 
     * @param linkId ID do link de pagamento
     * @param titulo Título personalizado
     * @param descricao Descrição personalizada
     * @param dataExpiracao Data de expiração
     * @param quantidadeUsos Quantidade máxima de usos
     * @param status Status do link
     * @param urlRetornoSucesso URL de retorno após sucesso
     * @param urlRetornoCancelamento URL de retorno após cancelamento
     * @return Link de pagamento atualizado
     */
    LinkPagamento atualizarLinkPagamento(Long linkId, String titulo, String descricao,
                                       LocalDateTime dataExpiracao, Integer quantidadeUsos,
                                       StatusLinkPagamentoEnum status,
                                       String urlRetornoSucesso, String urlRetornoCancelamento);
    
    /**
     * Altera o status de um link de pagamento
     * 
     * @param linkId ID do link de pagamento
     * @param status Novo status
     * @return true se a operação foi bem-sucedida
     */
    boolean alterarStatusLink(Long linkId, StatusLinkPagamentoEnum status);
    
    /**
     * Desativa um link de pagamento pelo código
     * 
     * @param codigo Código do link de pagamento
     * @param proprietarioId ID do proprietário
     * @throws IllegalArgumentException se o link não for encontrado ou o usuário não tiver permissão
     */
    void desativarLinkPagamento(String codigo, Long proprietarioId);
    
    /**
     * Incrementa o contador de usos de um link de pagamento
     * 
     * @param linkId ID do link de pagamento
     * @return true se a operação foi bem-sucedida
     */
    boolean incrementarUsos(Long linkId);
    
    /**
     * Exclui um link de pagamento
     * 
     * @param linkId ID do link de pagamento
     * @return true se a exclusão foi bem-sucedida
     */
    boolean excluirLinkPagamento(Long linkId);
    
    /**
     * Lista todos os links de pagamento de um usuário
     * 
     * @param proprietarioId ID do usuário proprietário
     * @return Lista de links de pagamento
     */
    List<LinkPagamento> listarLinksPorProprietario(Long proprietarioId);
    
    /**
     * Lista todos os links de pagamento de um produto
     * 
     * @param produtoId ID do produto
     * @return Lista de links de pagamento
     */
    List<LinkPagamento> listarLinksPorProduto(Long produtoId);
    
    /**
     * Busca um link de pagamento pelo ID
     * 
     * @param linkId ID do link de pagamento
     * @return Optional contendo o link, se encontrado
     */
    Optional<LinkPagamento> buscarLinkPorId(Long linkId);
    
    /**
     * Busca um link de pagamento pelo código
     * 
     * @param codigo Código único do link
     * @return Optional contendo o link, se encontrado
     */
    Optional<LinkPagamento> buscarLinkPorCodigo(String codigo);
    
    /**
     * Verifica se um link de pagamento pertence a um usuário
     * 
     * @param linkId ID do link de pagamento
     * @param proprietarioId ID do usuário
     * @return true se o link pertence ao usuário
     */
    boolean verificarPropriedadeLink(Long linkId, Long proprietarioId);
}
