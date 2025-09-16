package cv.ntinnovation.vintipay.adapter.out.persistence.repository;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.MetodoPagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MetodoPagamentoRepository extends JpaRepository<MetodoPagamentoEntity, Long> {
    Optional<MetodoPagamentoEntity> findByNameIgnoreCase(String name);
}
