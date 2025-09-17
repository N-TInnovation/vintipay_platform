package cv.ntinnovation.vintipay.adapter.in.web.mapper;

import cv.ntinnovation.vintipay.adapter.in.web.dto.DescontoCicloRequest;
import cv.ntinnovation.vintipay.adapter.in.web.dto.ProdutoRequest;
import cv.ntinnovation.vintipay.adapter.in.web.dto.ProdutoResponse;
import cv.ntinnovation.vintipay.application.core.domain.entity.Produto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProdutoWebMapper {

    private final DescontoCicloWebMapper descontoCicloWebMapper;

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
                // Campos de recorrência
                .recorrente(produto.isRecorrente())
                .periodoRecorrencia(produto.getPeriodoRecorrencia())
                .duracaoRecorrencia(produto.getDuracaoRecorrencia())
                .descricaoRecorrencia(produto.getDescricaoRecorrencia())
                // Nova configuração para primeira cobrança
                .primeiraCobranca(produto.getPrimeiraCobranca())
                // Descontos por ciclo
                .descontosPorCiclo(descontoCicloWebMapper.toResponseList(produto.getDescontosPorCiclo()))
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
                // Campos de recorrência
                .recorrente(request.isRecorrente())
                .periodoRecorrencia(request.getPeriodoRecorrencia())
                .duracaoRecorrencia(request.getDuracaoRecorrencia())
                .descricaoRecorrencia(request.getDescricaoRecorrencia())
                // Nova configuração para primeira cobrança
                .primeiraCobranca(request.getPrimeiraCobranca())
                // Descontos por ciclo
                .descontosPorCiclo(descontoCicloWebMapper.toDomainList(request.getDescontosPorCiclo()))
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
        
        ProdutoRequest request = ProdutoRequest.builder()
                .nome(produto.getNome())
                .descricao(produto.getDescricao())
                .preco(produto.getPreco())
                .imagem(produto.getImagem())
                .codigoSku(produto.getCodigoSku())
                .estoque(produto.getEstoque())
                .categoria(produto.getCategoria())
                .ativo(produto.isAtivo())
                .moeda(produto.getMoeda())
                // Campos de recorrência
                .recorrente(produto.isRecorrente())
                .periodoRecorrencia(produto.getPeriodoRecorrencia())
                .duracaoRecorrencia(produto.getDuracaoRecorrencia())
                .descricaoRecorrencia(produto.getDescricaoRecorrencia())
                // Nova configuração para primeira cobrança
                .primeiraCobranca(produto.getPrimeiraCobranca())
                .build();
                
        // Convertendo descontos por ciclo
        if (produto.getDescontosPorCiclo() != null && !produto.getDescontosPorCiclo().isEmpty()) {
            request.setDescontosPorCiclo(produto.getDescontosPorCiclo().stream()
                .map(desconto -> DescontoCicloRequest.builder()
                    .id(desconto.getId())
                    .numeroCiclo(desconto.getNumeroCiclo())
                    .percentualDesconto(desconto.getPercentualDesconto())
                    .descricao(desconto.getDescricao())
                    .build())
                .collect(Collectors.toList()));
        } else {
            request.setDescontosPorCiclo(new ArrayList<>());
        }
        
        return request;
    }
}
