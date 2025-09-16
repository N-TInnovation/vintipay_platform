package cv.ntinnovation.vintipay.adapter.out.persistence.adapter;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.ProdutoEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.mapper.LinkPagamentoMapper;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.LinkPagamentoRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.ProdutoRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.UsuarioRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.LinkPagamento;
import cv.ntinnovation.vintipay.application.port.out.BuscaLinkPagamento;
import cv.ntinnovation.vintipay.application.port.out.SalvaLinkPagamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LinkPagamentoAdapter implements SalvaLinkPagamento, BuscaLinkPagamento {

    private final LinkPagamentoRepository linkPagamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final LinkPagamentoMapper linkPagamentoMapper;

    @Override
    public LinkPagamento salvar(LinkPagamento linkPagamento) {
        var entity = linkPagamentoMapper.toEntity(linkPagamento);

        // Se o proprietário não foi carregado, mas temos o ID, vamos carregar
        if (entity.getProprietario() == null && linkPagamento.getProprietarioId() != null) {
            Optional<UsuarioEntity> proprietarioOpt = usuarioRepository.findById(linkPagamento.getProprietarioId());
            proprietarioOpt.ifPresent(entity::setProprietario);
        }

        // Se o produto não foi carregado, mas temos o ID, vamos carregar
        if (entity.getProduto() == null && linkPagamento.getProdutoId() != null) {
            Optional<ProdutoEntity> produtoOpt = produtoRepository.findById(linkPagamento.getProdutoId());
            produtoOpt.ifPresent(entity::setProduto);
        }

        var savedEntity = linkPagamentoRepository.save(entity);
        return linkPagamentoMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<LinkPagamento> buscarPorId(Long id) {
        return linkPagamentoRepository.findById(id)
                .map(linkPagamentoMapper::toDomain);
    }

    @Override
    public Optional<LinkPagamento> buscarPorCodigo(String codigo) {
        return linkPagamentoRepository.findByCodigo(codigo)
                .map(linkPagamentoMapper::toDomain);
    }

    @Override
    public List<LinkPagamento> buscarPorProprietarioId(Long proprietarioId) {
        return linkPagamentoRepository.findByProprietarioIdOrderByDataCriacaoDesc(proprietarioId)
                .stream()
                .map(linkPagamentoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<LinkPagamento> buscarPorProdutoId(Long produtoId) {
        return linkPagamentoRepository.findByProdutoIdOrderByDataCriacaoDesc(produtoId)
                .stream()
                .map(linkPagamentoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existePorId(Long id) {
        return linkPagamentoRepository.existsById(id);
    }

    @Override
    public boolean existePorIdEProprietarioId(Long id, Long proprietarioId) {
        return linkPagamentoRepository.existsByIdAndProprietarioId(id, proprietarioId);
    }

    
}


