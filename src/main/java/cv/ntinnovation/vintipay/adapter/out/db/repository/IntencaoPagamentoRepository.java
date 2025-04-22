package cv.ntinnovation.vintipay.adapter.out.db.repository;

import cv.ntinnovation.vintipay.adapter.out.db.entity.IntencaoPagamentoEntity;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IntencaoPagamentoRepository extends JpaRepository<IntencaoPagamentoEntity, Long> {
    
    Optional<IntencaoPagamentoEntity> findByReference(String reference);
    
    List<IntencaoPagamentoEntity> findByStatus(StatusPagamentoEnum status);
    
    List<IntencaoPagamentoEntity> findByContaPagamentoId(Long contaPagamentoId);
}
