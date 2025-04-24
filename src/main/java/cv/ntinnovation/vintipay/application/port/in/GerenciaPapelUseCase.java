package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.Papel;
import java.util.List;
import java.util.Optional;

public interface GerenciaPapelUseCase {
    Papel criar(Papel papel);
    Papel atualizar(Long id, Papel papel);
    void excluir(Long id);
    Optional<Papel> buscarPorId(Long id);
    Optional<Papel> buscarPorCodigo(String codigo);
    Optional<Papel> buscarPapelPadrao();
    List<Papel> listarTodos();
    boolean existePorCodigo(String codigo);
    Papel adicionarPermissao(Long papelId, Long permissaoId);
    Papel removerPermissao(Long papelId, Long permissaoId);
}
