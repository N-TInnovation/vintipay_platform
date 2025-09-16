package cv.ntinnovation.vintipay.adapter.out.persistence.repository;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.PlanoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanoRepository extends JpaRepository<PlanoEntity, Long> {
    List<PlanoEntity> findByAtivoTrue();
    Optional<PlanoEntity> findByNome(String nome);
    Optional<PlanoEntity> findByStripeProductId(String stripeProductId);
}
