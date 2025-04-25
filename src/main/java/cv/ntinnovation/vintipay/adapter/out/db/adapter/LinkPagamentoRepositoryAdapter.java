package cv.ntinnovation.vintipay.adapter.out.db.adapter;

import cv.ntinnovation.vintipay.adapter.out.db.entity.LinkPagamentoEntity;
import cv.ntinnovation.vintipay.adapter.out.db.mapper.LinkPagamentoMapper;
import cv.ntinnovation.vintipay.adapter.out.db.repository.LinkPagamentoRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.LinkPagamento;
import cv.ntinnovation.vintipay.application.port.out.BuscaLinkPagamento;
import cv.ntinnovation.vintipay.application.port.out.SalvaLinkPagamento;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Primary
@Component
@RequiredArgsConstructor
public class LinkPagamentoRepositoryAdapter implements SalvaLinkPagamento, BuscaLinkPagamento {

    private final LinkPagamentoRepository linkPagamentoRepository;
    private final LinkPagamentoMapper linkPagamentoMapper;

    @Override
    public LinkPagamento salvar(LinkPagamento linkPagamento) {
        LinkPagamentoEntity linkPagamentoEntity = linkPagamentoMapper.toEntity(linkPagamento);
        linkPagamentoEntity = linkPagamentoRepository.save(linkPagamentoEntity);
        return linkPagamentoMapper.toDomain(linkPagamentoEntity);
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
