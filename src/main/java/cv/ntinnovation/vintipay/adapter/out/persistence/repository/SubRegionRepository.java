package cv.ntinnovation.vintipay.adapter.out.persistence.repository;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.SubRegionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para acesso a dados de sub-regiões
 */
@Repository
public interface SubRegionRepository extends JpaRepository<SubRegionEntity, Long> {
    
    /**
     * Busca uma sub-região pelo nome
     */
    Optional<SubRegionEntity> findByName(String name);
    
    /**
     * Busca sub-regiões pelo ID da região
     */
    List<SubRegionEntity> findByRegionId(Long regionId);
    
    /**
     * Busca uma sub-região pelo nome e ID da região
     */
    Optional<SubRegionEntity> findByNameAndRegionId(String name, Long regionId);
}
