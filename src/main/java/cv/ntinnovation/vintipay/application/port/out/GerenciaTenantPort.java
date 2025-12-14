package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.Tenant;
import java.util.List;
import java.util.Optional;

public interface GerenciaTenantPort {
    Tenant salvar(Tenant tenant);
    void excluir(Long id);
    Optional<Tenant> buscarPorId(Long id);
    Optional<Tenant> buscarPorSlug(String slug);
    Optional<Tenant> buscarPorCodigo(String codigo);
    List<Tenant> buscarPorSlugIn(List<String> slugs);
    boolean existePorSlug(String slug);
    List<Tenant> listarTodos();
}
