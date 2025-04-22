package cv.ntinnovation.vintipay.adapter.out.db.repository;

import cv.ntinnovation.vintipay.adapter.out.db.entity.PlanoAssinaturaEntity;
import cv.ntinnovation.vintipay.adapter.out.db.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanoAssinaturaRepository extends JpaRepository<PlanoAssinaturaEntity, Long> {
    Optional<PlanoAssinaturaEntity> findByUsuario(UsuarioEntity usuario);
    Optional<PlanoAssinaturaEntity> findByStripeSubscriptionId(String stripeSubscriptionId);
    List<PlanoAssinaturaEntity> findByStatus(PlanoAssinaturaEntity.StatusAssinatura status);
}
