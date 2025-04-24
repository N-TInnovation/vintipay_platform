package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.LinkPagamento;

import java.util.List;
import java.util.Optional;

public interface BuscaLinkPagamento {
    Optional<LinkPagamento> buscarPorId(Long id);
    Optional<LinkPagamento> buscarPorCodigo(String codigo);
    List<LinkPagamento> buscarPorProprietarioId(Long proprietarioId);
    List<LinkPagamento> buscarPorProdutoId(Long produtoId);
    boolean existePorId(Long id);
    boolean existePorIdEProprietarioId(Long id, Long proprietarioId);
}
