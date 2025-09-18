package cv.ntinnovation.vintipay.adapter.out.persistence.repository;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.SubscricaoPagamentoEntity;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusSubscricaoPagamentoEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscricaoPagamentoRepository extends JpaRepository<SubscricaoPagamentoEntity, Long> {
    
    Optional<SubscricaoPagamentoEntity> findByReference(String reference);
    
    List<SubscricaoPagamentoEntity> findByStatus(StatusSubscricaoPagamentoEnum status);
    
    Optional<SubscricaoPagamentoEntity> findByIntencaoPagamentoAtualId(Long intencaoPagamentoId);
}
