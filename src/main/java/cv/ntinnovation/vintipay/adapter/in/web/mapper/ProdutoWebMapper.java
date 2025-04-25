package cv.ntinnovation.vintipay.adapter.in.web.mapper;

import cv.ntinnovation.vintipay.adapter.in.web.dto.ProdutoRequest;
import cv.ntinnovation.vintipay.adapter.in.web.dto.ProdutoResponse;
import cv.ntinnovation.vintipay.application.core.domain.entity.Produto;
import org.springframework.stereotype.Component;

@Component
public class ProdutoWebMapper {

    /**
     * Converte uma entidade Produto para um DTO ProdutoResponse
     * 
     * @param produto Entidade a ser convertida
     * @return DTO convertido
     */
    public ProdutoResponse toProdutoResponse(Produto produto) {
        if (produto == null) {
            return null;
        }
        
        return ProdutoResponse.builder()
                .id(produto.getId())
                .nome(produto.getNome())
                .descricao(produto.getDescricao())
                .preco(produto.getPreco())
                .imagem(produto.getImagem())
                .codigoSku(produto.getCodigoSku())
                .estoque(produto.getEstoque())
                .categoria(produto.getCategoria())
                .ativo(produto.isAtivo())
                .dataCriacao(produto.getDataCriacao())
                .dataAtualizacao(produto.getDataAtualizacao())
                .moeda(produto.getMoeda())
                .proprietarioId(produto.getProprietarioId())
                .build();
    }
    
    /**
     * Converte um DTO ProdutoRequest para uma entidade Produto
     * 
     * @param request DTO a ser convertido
     * @return Entidade convertida
     */
    public Produto toProduto(ProdutoRequest request) {
        if (request == null) {
            return null;
        }
        
        return Produto.builder()
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .preco(request.getPreco())
                .imagem(request.getImagem())
                .codigoSku(request.getCodigoSku())
                .estoque(request.getEstoque())
                .categoria(request.getCategoria())
                .ativo(request.isAtivo())
                .moeda(request.getMoeda())
                .build();
    }
    
    /**
     * Converte uma entidade Produto para um DTO ProdutoRequest
     * 
     * @param produto Entidade a ser convertida
     * @return DTO convertido
     */
    public ProdutoRequest toProdutoRequest(Produto produto) {
        if (produto == null) {
            return null;
        }
        
        return ProdutoRequest.builder()
                .nome(produto.getNome())
                .descricao(produto.getDescricao())
                .preco(produto.getPreco())
                .imagem(produto.getImagem())
                .codigoSku(produto.getCodigoSku())
                .estoque(produto.getEstoque())
                .categoria(produto.getCategoria())
                .ativo(produto.isAtivo())
                .moeda(produto.getMoeda())
                .build();
    }
}
