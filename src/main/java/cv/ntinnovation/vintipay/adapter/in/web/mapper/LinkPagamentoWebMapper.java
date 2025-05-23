package cv.ntinnovation.vintipay.adapter.in.web.mapper;

import cv.ntinnovation.vintipay.adapter.in.web.dto.LinkPagamentoRequest;
import cv.ntinnovation.vintipay.adapter.in.web.dto.LinkPagamentoResponse;
import cv.ntinnovation.vintipay.application.core.domain.entity.LinkPagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusLinkPagamentoEnum;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.Locale;

@Component
public class LinkPagamentoWebMapper {

    /**
     * Converte uma entidade LinkPagamento para um DTO LinkPagamentoResponse
     * 
     * @param link Entidade a ser convertida
     * @return DTO convertido
     */
    public LinkPagamentoResponse toLinkPagamentoResponse(LinkPagamento link) {
        if (link == null) {
            return null;
        }
        
        // Formatar o preço do produto
        String precoFormatado = "";
        if (link.getProduto() != null && link.getProduto().getPreco() != null) {
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("pt", "CV"));
            precoFormatado = format.format(link.getProduto().getPreco());
        }
        
        return LinkPagamentoResponse.builder()
                .id(link.getId())
                .codigo(link.getCodigo())
                .titulo(link.getTitulo())
                .descricao(link.getDescricao())
                .status(link.getStatus())
                .dataCriacao(link.getDataCriacao())
                .dataExpiracao(link.getDataExpiracao())
                .quantidadeUsos(link.getQuantidadeUsos())
                .usos(link.getUsos())
                .produtoId(link.getProdutoId())
                .produtoNome(link.getProduto() != null ? link.getProduto().getNome() : null)
                .produtoDescricao(link.getProduto() != null ? link.getProduto().getDescricao() : null)
                .produtoImagem(link.getProduto() != null ? link.getProduto().getImagem() : null)
                .produtoPreco(precoFormatado)
                .produtoMoeda(link.getProduto() != null ? link.getProduto().getMoeda() : "CVE")
                .proprietarioId(link.getProprietarioId())
                .urlRetornoSucesso(link.getUrlRetornoSucesso())
                .urlRetornoCancelamento(link.getUrlRetornoCancelamento())
                .urlCheckout("/links-pagamento/p/" + link.getCodigo())
                .disponivel(link.isDisponivel())
                .build();
    }
    
    /**
     * Converte um DTO LinkPagamentoRequest para uma entidade LinkPagamento
     * 
     * @param request DTO a ser convertido
     * @return Entidade convertida
     */
    public LinkPagamento toLinkPagamento(LinkPagamentoRequest request) {
        if (request == null) {
            return null;
        }
        
        return LinkPagamento.builder()
                .titulo(request.getNome())
                .descricao(request.getDescricao())
                .dataExpiracao(request.getDataExpiracao())
                .quantidadeUsos(request.getQuantidadeUsos())
                .status(request.getStatus())
                .produtoId(request.getProdutoId())
                .urlRetornoSucesso(request.getUrlRetornoSucesso())
                .urlRetornoCancelamento(request.getUrlRetornoCancelamento())
                .build();
    }
    
    /**
     * Converte uma entidade LinkPagamento para um DTO LinkPagamentoRequest
     * 
     * @param link Entidade a ser convertida
     * @return DTO convertido
     */
    public LinkPagamentoRequest toLinkPagamentoRequest(LinkPagamento link) {
        if (link == null) {
            return null;
        }
        
        return LinkPagamentoRequest.builder()
                .produtoId(link.getProdutoId())
                .configuracaoPagamentoId(link.getConfiguracaoPagamentoId())
                .nome(link.getTitulo())
                .descricao(link.getDescricao())
                .dataExpiracao(link.getDataExpiracao())
                .quantidadeUsos(link.getQuantidadeUsos())
                .status(link.getStatus())
                .urlRetornoSucesso(link.getUrlRetornoSucesso())
                .urlRetornoCancelamento(link.getUrlRetornoCancelamento())
                .ativo(link.getStatus() == StatusLinkPagamentoEnum.ATIVO)
                .build();
    }
}
