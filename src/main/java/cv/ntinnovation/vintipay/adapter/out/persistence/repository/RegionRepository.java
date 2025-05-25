package cv.ntinnovation.vintipay.adapter.out.persistence.repository;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.RegionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para acesso a dados de regiões
 */
@Repository
public interface RegionRepository extends JpaRepository<RegionEntity, Long> {
    
    /**
     * Busca uma região pelo nome
     */
    Optional<RegionEntity> findByName(String name);
}
