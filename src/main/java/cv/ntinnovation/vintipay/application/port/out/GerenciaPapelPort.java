package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.Papel;
import java.util.List;
import java.util.Optional;

public interface GerenciaPapelPort {
    Papel salvar(Papel papel);
    void excluir(Long id);
    Optional<Papel> buscarPorId(Long id);
    Optional<Papel> buscarPorCodigo(String codigo);
    Optional<Papel> buscarPapelPadrao();
    List<Papel> listarTodos();
    boolean existePorCodigo(String codigo);
}
