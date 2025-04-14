package cv.ntinnovation.gateway_payment.adapter.out.db.repository;

import cv.ntinnovation.gateway_payment.adapter.out.db.entity.PagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<PagamentoEntity, Long> {
    Optional<PagamentoEntity> findByIntencaoPagamentoReference(String reference);
}
