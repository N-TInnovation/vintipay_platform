package cv.ntinnovation.vintipay.adapter.out.db.repository;

import cv.ntinnovation.vintipay.adapter.out.db.entity.SessaoPagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessaoPagamentoRepository extends JpaRepository<SessaoPagamentoEntity, Long> {
    
    Optional<SessaoPagamentoEntity> findBySessionId(String sessionId);
    
    Optional<SessaoPagamentoEntity> findByReferenceAndValidTrue(String reference);
    
    @Query("SELECT s FROM SessaoPagamentoEntity s WHERE s.reference = :reference AND s.valid = true AND s.expiresAt > :now")
    Optional<SessaoPagamentoEntity> findValidSessionByReference(String reference, LocalDateTime now);
    
    List<SessaoPagamentoEntity> findByClientId(String clientId);
    
    @Query("SELECT s FROM SessaoPagamentoEntity s WHERE s.expiresAt < :now AND s.valid = true")
    List<SessaoPagamentoEntity> findExpiredSessions(LocalDateTime now);
}
