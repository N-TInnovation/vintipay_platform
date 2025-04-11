package cv.ntinnovation.gateway_payment.adapter.out.db.repository;

import cv.ntinnovation.gateway_payment.adapter.out.db.entity.MetodoPagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MetodoPagamentoRepository extends JpaRepository<MetodoPagamentoEntity, Long> {
    Optional<MetodoPagamentoEntity> findByNameIgnoreCase(String name);
}
