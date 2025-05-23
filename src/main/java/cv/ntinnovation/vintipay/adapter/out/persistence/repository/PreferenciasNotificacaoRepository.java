package cv.ntinnovation.vintipay.adapter.out.persistence.repository;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.PreferenciasNotificacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para operações de banco de dados relacionadas às preferências de notificação
 */
@Repository
public interface PreferenciasNotificacaoRepository extends JpaRepository<PreferenciasNotificacaoEntity, Long> {
    
    /**
     * Busca preferências de notificação por ID do usuário
     * 
     * @param usuarioId ID do usuário
     * @return Preferências de notificação encontradas
     */
    Optional<PreferenciasNotificacaoEntity> findByUsuarioId(Long usuarioId);
}
