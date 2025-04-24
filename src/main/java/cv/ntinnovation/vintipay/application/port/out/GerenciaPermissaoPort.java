package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.Permissao;
import java.util.List;
import java.util.Optional;

public interface GerenciaPermissaoPort {
    Permissao salvar(Permissao permissao);
    void excluir(Long id);
    Optional<Permissao> buscarPorId(Long id);
    Optional<Permissao> buscarPorCodigo(String codigo);
    List<Permissao> listarTodas();
    boolean existePorCodigo(String codigo);
}
