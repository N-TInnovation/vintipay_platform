package cv.ntinnovation.vintipay.adapter.out.db.repository;

import cv.ntinnovation.vintipay.adapter.out.db.entity.PapelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PapelRepository extends JpaRepository<PapelEntity, Long> {
    Optional<PapelEntity> findByCodigo(String codigo);
    Optional<PapelEntity> findByPadraoTrue();
    boolean existsByCodigo(String codigo);
}
