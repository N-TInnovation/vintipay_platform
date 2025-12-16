package cv.ntinnovation.vintipay.adapter.out.persistence.repository;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TenantRepository extends JpaRepository<TenantEntity, Long> {
    Optional<TenantEntity> findBySlug(String slug);
    Optional<TenantEntity> findByCodigo(String codigo);
    List<TenantEntity> findBySlugIn(List<String> slugs);
    boolean existsBySlug(String slug);
    List<TenantEntity> findByUsuariosId(Long usuarioId);
}
