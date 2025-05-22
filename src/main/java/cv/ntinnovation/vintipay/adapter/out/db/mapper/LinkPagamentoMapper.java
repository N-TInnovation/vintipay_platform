package cv.ntinnovation.vintipay.adapter.out.db.mapper;

import cv.ntinnovation.vintipay.adapter.out.db.entity.ConfiguracaoPagamentoEntity;
import cv.ntinnovation.vintipay.adapter.out.db.entity.LinkPagamentoEntity;
import cv.ntinnovation.vintipay.adapter.out.db.entity.ProdutoEntity;
import cv.ntinnovation.vintipay.adapter.out.db.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.LinkPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Produto;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LinkPagamentoMapper {
    
    private final UsuarioMapper usuarioMapper;
    private final ProdutoMapper produtoMapper;
    private final ConfiguracaoPagamentoMapper configuracaoPagamentoMapper;
    
    /**
     * Converte uma entidade de domínio para uma entidade JPA
     * 
     * @param link Entidade de domínio
     * @return Entidade JPA
     */
    public LinkPagamentoEntity toEntity(LinkPagamento link) {
        if (link == null) {
            return null;
        }
        
        UsuarioEntity proprietario = null;
        if (link.getProprietario() != null) {
            proprietario = usuarioMapper.toEntity(link.getProprietario());
        }
        
        ProdutoEntity produto = null;
        if (link.getProduto() != null) {
            produto = produtoMapper.toEntity(link.getProduto());
        }

        ConfiguracaoPagamentoEntity configuracaoPagamento = null;
        if (link.getConfiguracaoPagamento() != null) {
            configuracaoPagamento = configuracaoPagamentoMapper.toEntity(link.getConfiguracaoPagamento());
        }
        
        return LinkPagamentoEntity.builder()
                .id(link.getId())
                .codigo(link.getCodigo())
                .titulo(link.getTitulo())
                .descricao(link.getDescricao())
                .status(link.getStatus())
                .dataCriacao(link.getDataCriacao())
                .dataExpiracao(link.getDataExpiracao())
                .quantidadeUsos(link.getQuantidadeUsos())
                .usos(link.getUsos())
                .produto(produto)
                .proprietario(proprietario)
                .urlRetornoSucesso(link.getUrlRetornoSucesso())
                .urlRetornoCancelamento(link.getUrlRetornoCancelamento())
                .configuracaoPagamento(configuracaoPagamento)
                .build();
    }
    
    /**
     * Converte uma entidade JPA para uma entidade de domínio
     * 
     * @param entity Entidade JPA
     * @return Entidade de domínio
     */
    public LinkPagamento toDomain(LinkPagamentoEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Usuario proprietario = null;
        if (entity.getProprietario() != null) {
            proprietario = usuarioMapper.toDomain(entity.getProprietario());
        }
        
        Produto produto = null;
        if (entity.getProduto() != null) {
            produto = produtoMapper.toDomain(entity.getProduto());
        }

        ConfiguracaoPagamento configuracaoPagamento = null;
        if (entity.getProduto() != null) {
            configuracaoPagamento = configuracaoPagamentoMapper.toDomain(entity.getConfiguracaoPagamento());
        }
        
        return LinkPagamento.builder()
                .id(entity.getId())
                .codigo(entity.getCodigo())
                .titulo(entity.getTitulo())
                .descricao(entity.getDescricao())
                .status(entity.getStatus())
                .dataCriacao(entity.getDataCriacao())
                .dataExpiracao(entity.getDataExpiracao())
                .quantidadeUsos(entity.getQuantidadeUsos())
                .usos(entity.getUsos())
                .produto(produto)
                .produtoId(entity.getProduto() != null ? entity.getProduto().getId() : null)
                .proprietario(proprietario)
                .proprietarioId(entity.getProprietario() != null ? entity.getProprietario().getId() : null)
                .urlRetornoSucesso(entity.getUrlRetornoSucesso())
                .urlRetornoCancelamento(entity.getUrlRetornoCancelamento())
                .configuracaoPagamento(configuracaoPagamento)
                .build();
    }
}
