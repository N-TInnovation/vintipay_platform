package cv.ntinnovation.vintipay.adapter.out.persistence.mapper;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.TenantEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.Tenant;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class TenantMapper {

    private final UsuarioMapper usuarioMapper;
    private final ConfiguracaoPagamentoMapper configuracaoPagamentoMapper;
    private final ProdutoMapper produtoMapper;
    private final LinkPagamentoMapper linkPagamentoMapper;
    private final PlanoMapper planoMapper;
    
    public TenantMapper(
            @Lazy UsuarioMapper usuarioMapper,
            @Lazy ConfiguracaoPagamentoMapper configuracaoPagamentoMapper,
            @Lazy ProdutoMapper produtoMapper,
            @Lazy LinkPagamentoMapper linkPagamentoMapper,
            @Lazy PlanoMapper planoMapper) {
        this.usuarioMapper = usuarioMapper;
        this.configuracaoPagamentoMapper = configuracaoPagamentoMapper;
        this.produtoMapper = produtoMapper;
        this.linkPagamentoMapper = linkPagamentoMapper;
        this.planoMapper = planoMapper;
    }

    public TenantEntity toEntity(Tenant domain) {
        if (domain == null) {
            return null;
        }

        return TenantEntity.builder()
                .id(domain.getId())
                .codigo(domain.getCodigo())
                .slug(domain.getSlug())
                .nome(domain.getNome())
                .nomeFantasia(domain.getNomeFantasia())
                .documento(domain.getDocumento())
                .email(domain.getEmail())
                .telefone(domain.getTelefone())
                .endereco(domain.getEndereco())
                .cidade(domain.getCidade())
                .pais(domain.getPais())
                .ativo(domain.getAtivo() != null ? domain.getAtivo() : true)
                .logoUrl(domain.getLogoUrl())
                .configuracoes(domain.getConfiguracoes())
                .observacoes(domain.getObservacoes())
                .createdAt(domain.getCreatedAt() != null ? domain.getCreatedAt() : LocalDateTime.now())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public Tenant toDomain(TenantEntity entity) {
        if (entity == null) {
            return null;
        }

        Tenant.TenantBuilder builder = Tenant.builder()
                .id(entity.getId())
                .codigo(entity.getCodigo())
                .slug(entity.getSlug())
                .nome(entity.getNome())
                .nomeFantasia(entity.getNomeFantasia())
                .documento(entity.getDocumento())
                .email(entity.getEmail())
                .telefone(entity.getTelefone())
                .endereco(entity.getEndereco())
                .cidade(entity.getCidade())
                .pais(entity.getPais())
                .ativo(entity.getAtivo())
                .logoUrl(entity.getLogoUrl())
                .configuracoes(entity.getConfiguracoes())
                .observacoes(entity.getObservacoes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt());

        Tenant tenant = builder.build();

        if (entity.getUsuarios() != null && !entity.getUsuarios().isEmpty()) {
            try {
                tenant.setUsuarios(entity.getUsuarios().stream()
                        .map(usuarioMapper::toDomain)
                        .collect(Collectors.toList()));
            } catch (Exception e) {
                tenant.setUsuarios(Collections.emptyList());
            }
        }

        if (entity.getConfiguracoesPagamento() != null && !entity.getConfiguracoesPagamento().isEmpty()) {
            try {
                tenant.setConfiguracoesPagamento(entity.getConfiguracoesPagamento().stream()
                        .map(configuracaoPagamentoMapper::toDomain)
                        .collect(Collectors.toList()));
            } catch (Exception e) {
                tenant.setConfiguracoesPagamento(Collections.emptyList());
            }
        }

        if (entity.getProdutos() != null && !entity.getProdutos().isEmpty()) {
            try {
                tenant.setProdutos(entity.getProdutos().stream()
                        .map(produtoMapper::toDomain)
                        .collect(Collectors.toList()));
            } catch (Exception e) {
                tenant.setProdutos(Collections.emptyList());
            }
        }

        if (entity.getLinksPagamento() != null && !entity.getLinksPagamento().isEmpty()) {
            try {
                tenant.setLinksPagamento(entity.getLinksPagamento().stream()
                        .map(linkPagamentoMapper::toDomain)
                        .collect(Collectors.toList()));
            } catch (Exception e) {
                tenant.setLinksPagamento(Collections.emptyList());
            }
        }

        if (entity.getPlanos() != null && !entity.getPlanos().isEmpty()) {
            try {
                tenant.setPlanos(entity.getPlanos().stream()
                        .map(planoMapper::toDomain)
                        .collect(Collectors.toList()));
            } catch (Exception e) {
                tenant.setPlanos(Collections.emptyList());
            }
        }

        return tenant;
    }

    public Tenant toDomainWithoutRelationships(TenantEntity entity) {
        if (entity == null) {
            return null;
        }

        return Tenant.builder()
                .id(entity.getId())
                .codigo(entity.getCodigo())
                .slug(entity.getSlug())
                .nome(entity.getNome())
                .nomeFantasia(entity.getNomeFantasia())
                .documento(entity.getDocumento())
                .email(entity.getEmail())
                .telefone(entity.getTelefone())
                .endereco(entity.getEndereco())
                .cidade(entity.getCidade())
                .pais(entity.getPais())
                .ativo(entity.getAtivo())
                .logoUrl(entity.getLogoUrl())
                .configuracoes(entity.getConfiguracoes())
                .observacoes(entity.getObservacoes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
