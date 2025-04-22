package cv.ntinnovation.vintipay.adapter.out.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cv.ntinnovation.vintipay.adapter.out.db.entity.ContaPagamentoEntity;

import java.util.Optional;

@Repository
public interface ContaPagamentoRepository extends JpaRepository<ContaPagamentoEntity, Long> {
    Optional<ContaPagamentoEntity> findByEmail(String email);
}
