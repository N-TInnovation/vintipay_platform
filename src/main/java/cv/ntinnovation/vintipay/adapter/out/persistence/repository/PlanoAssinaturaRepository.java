package cv.ntinnovation.vintipay.adapter.out.persistence.repository;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.PlanoAssinaturaEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanoAssinaturaRepository extends JpaRepository<PlanoAssinaturaEntity, Long> {
    Optional<PlanoAssinaturaEntity> findByUsuario(UsuarioEntity usuario);
    Optional<PlanoAssinaturaEntity> findByStripeSubscriptionId(String stripeSubscriptionId);
    List<PlanoAssinaturaEntity> findByStatus(PlanoAssinaturaEntity.StatusAssinatura status);
    
    /**
     * Busca uma assinatura pelo ID do usuário
     * @param usuarioId ID do usuário
     * @return Optional contendo a assinatura se encontrada
     */
    @Query("SELECT p FROM PlanoAssinaturaEntity p WHERE p.usuario.id = :usuarioId")
    Optional<PlanoAssinaturaEntity> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}
