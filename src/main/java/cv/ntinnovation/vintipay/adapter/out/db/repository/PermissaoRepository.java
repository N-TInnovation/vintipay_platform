package cv.ntinnovation.vintipay.adapter.out.db.repository;

import cv.ntinnovation.vintipay.adapter.out.db.entity.PermissaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissaoRepository extends JpaRepository<PermissaoEntity, Long> {
    Optional<PermissaoEntity> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
}
