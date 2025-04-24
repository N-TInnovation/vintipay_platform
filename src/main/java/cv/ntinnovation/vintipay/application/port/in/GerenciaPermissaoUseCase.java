package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.Permissao;
import java.util.List;
import java.util.Optional;

public interface GerenciaPermissaoUseCase {
    Permissao criar(Permissao permissao);
    Permissao atualizar(Long id, Permissao permissao);
    void excluir(Long id);
    Optional<Permissao> buscarPorId(Long id);
    Optional<Permissao> buscarPorCodigo(String codigo);
    List<Permissao> listarTodas();
    boolean existePorCodigo(String codigo);
}
