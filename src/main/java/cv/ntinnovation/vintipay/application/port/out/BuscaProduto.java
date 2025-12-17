package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.Produto;

import java.util.List;
import java.util.Optional;

public interface BuscaProduto {
    Optional<Produto> buscarPorId(Long id);
    List<Produto> buscarPorProprietarioId(Long proprietarioId);
    List<Produto> buscarPorProprietarioIdETenantId(Long proprietarioId, Long tenantId);
    List<Produto> buscarPorTenantId(Long tenantId);
    boolean existePorId(Long id);
    boolean existePorIdEProprietarioId(Long id, Long proprietarioId);
    List<Produto> buscarTodosAtivos();
}
