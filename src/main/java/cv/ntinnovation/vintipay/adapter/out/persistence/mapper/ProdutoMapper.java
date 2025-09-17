package cv.ntinnovation.vintipay.adapter.out.persistence.mapper;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.ProdutoEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.Produto;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProdutoMapper {
    
    private final UsuarioMapper usuarioMapper;
    private final DescontoCicloMapper descontoCicloMapper;
    
    /**
     * Converte uma entidade de domínio para uma entidade JPA
     * 
     * @param produto Entidade de domínio
     * @return Entidade JPA
     */
    public ProdutoEntity toEntity(Produto produto) {
        if (produto == null) {
            return null;
        }
        
        UsuarioEntity proprietario = null;
        if (produto.getProprietario() != null) {
            proprietario = usuarioMapper.toEntity(produto.getProprietario());
        }
        
        ProdutoEntity entity = ProdutoEntity.builder()
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
                .proprietario(proprietario)
                // Campos de recorrência
                .recorrente(produto.isRecorrente())
                .periodoRecorrencia(produto.getPeriodoRecorrencia())
                .duracaoRecorrencia(produto.getDuracaoRecorrencia())
                .descricaoRecorrencia(produto.getDescricaoRecorrencia())
                // Nova configuração para primeira cobrança
                .primeiraCobranca(produto.getPrimeiraCobranca())
                .build();
        
        // Adicionar os descontos por ciclo
        if (produto.getDescontosPorCiclo() != null && !produto.getDescontosPorCiclo().isEmpty()) {
            produto.getDescontosPorCiclo().forEach(desconto -> 
                entity.adicionarDescontoPorCiclo(descontoCicloMapper.toEntity(desconto, entity)));
        }
        
        return entity;
    }
    
    /**
     * Converte uma entidade JPA para uma entidade de domínio
     * 
     * @param entity Entidade JPA
     * @return Entidade de domínio
     */
    public Produto toDomain(ProdutoEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Usuario proprietario = null;
        if (entity.getProprietario() != null) {
            proprietario = usuarioMapper.toDomain(entity.getProprietario());
        }
        
        return Produto.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .preco(entity.getPreco())
                .imagem(entity.getImagem())
                .codigoSku(entity.getCodigoSku())
                .estoque(entity.getEstoque())
                .categoria(entity.getCategoria())
                .ativo(entity.isAtivo())
                .dataCriacao(entity.getDataCriacao())
                .dataAtualizacao(entity.getDataAtualizacao())
                .moeda(entity.getMoeda())
                .proprietario(proprietario)
                .proprietarioId(entity.getProprietario() != null ? entity.getProprietario().getId() : null)
                // Campos de recorrência
                .recorrente(entity.isRecorrente())
                .periodoRecorrencia(entity.getPeriodoRecorrencia())
                .duracaoRecorrencia(entity.getDuracaoRecorrencia())
                .descricaoRecorrencia(entity.getDescricaoRecorrencia())
                // Nova configuração para primeira cobrança
                .primeiraCobranca(entity.getPrimeiraCobranca())
                // Descontos por ciclo
                .descontosPorCiclo(descontoCicloMapper.toDomainList(entity.getDescontosPorCiclo()))
                .build();
    }
}
