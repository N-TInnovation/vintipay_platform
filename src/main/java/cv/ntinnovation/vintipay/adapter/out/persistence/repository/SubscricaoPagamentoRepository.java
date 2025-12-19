package cv.ntinnovation.vintipay.adapter.out.persistence.repository;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.SubscricaoPagamentoEntity;
import cv.ntinnovation.vintipay.application.core.domain.enums.AmbienteEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusSubscricaoPagamentoEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscricaoPagamentoRepository extends JpaRepository<SubscricaoPagamentoEntity, Long> {
    
    Optional<SubscricaoPagamentoEntity> findByReference(String reference);
    
    List<SubscricaoPagamentoEntity> findByStatus(StatusSubscricaoPagamentoEnum status);
    
    Optional<SubscricaoPagamentoEntity> findByIntencaoPagamentoAtualId(Long intencaoPagamentoId);
    
    @Query("SELECT s FROM SubscricaoPagamentoEntity s WHERE s.tenantId = :tenantId AND s.ambiente = :ambiente")
    Page<SubscricaoPagamentoEntity> findByTenantIdAndAmbiente(@Param("tenantId") Long tenantId, @Param("ambiente") AmbienteEnum ambiente, Pageable pageable);
    
    @Query("SELECT s FROM SubscricaoPagamentoEntity s WHERE s.tenantId = :tenantId AND s.ambiente = :ambiente AND s.status = :status")
    Page<SubscricaoPagamentoEntity> findByTenantIdAndAmbienteAndStatus(@Param("tenantId") Long tenantId, @Param("ambiente") AmbienteEnum ambiente, @Param("status") StatusSubscricaoPagamentoEnum status, Pageable pageable);
}
