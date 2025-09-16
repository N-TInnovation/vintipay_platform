package cv.ntinnovation.vintipay.adapter.out.persistence.repository;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.PagamentoEntity;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<PagamentoEntity, Long> {
    Optional<PagamentoEntity> findByReference(String reference);

    @Query("SELECT p FROM PagamentoEntity p WHERE " +
           "(:reference IS NULL OR LOWER(p.reference) LIKE LOWER(CONCAT('%', :reference, '%'))) AND " +
           "(:status IS NULL OR p.status = :status)")
    Page<PagamentoEntity> findByReferenceContainingIgnoreCaseAndStatusIfPresent(
            @Param("reference") String reference,
            @Param("status") StatusPagamentoEnum status,
            Pageable pageable);

    @Query("SELECT p FROM PagamentoEntity p WHERE :status IS NULL OR p.status = :status")
    Page<PagamentoEntity> findByStatusIfPresent(@Param("status") StatusPagamentoEnum status, Pageable pageable);

    @Query(value = "SELECT p.* FROM pagamento p " +
           "JOIN intencao_pagamento ip ON p.intencao_pagamento_id = ip.id " +
           "JOIN conta_pagamento cp ON ip.conta_pagamento_id = cp.id " +
           "JOIN configuracao_pagamento cfp ON ip.pos_id = cfp.pos_id " +
           "JOIN usuarios u ON cfp.usuario_id = u.id " +
           "WHERE u.id = :usuarioId " +
           "AND (:status IS NULL OR p.status = :status) " +
           "ORDER BY created_at DESC",
           countQuery = "SELECT COUNT(*) FROM pagamento p " +
           "JOIN intencao_pagamento ip ON p.intencao_pagamento_id = ip.id " +
           "JOIN conta_pagamento cp ON ip.conta_pagamento_id = cp.id " +
           "JOIN configuracao_pagamento cfp ON ip.pos_id = cfp.pos_id " +
           "JOIN usuarios u ON cfp.usuario_id = u.id " +
           "WHERE u.id = :usuarioId " +
           "AND (:status IS NULL OR p.status = :status)",
           nativeQuery = true)
    Page<PagamentoEntity> findByUsuarioIdAndStatusIfPresent(
            @Param("usuarioId") Long usuarioId,
            @Param("status") String status,
            Pageable pageable);

    @Query(value = "SELECT p.* FROM pagamento p " +
           "JOIN intencao_pagamento ip ON p.intencao_pagamento_id = ip.id " +
           "JOIN conta_pagamento cp ON ip.conta_pagamento_id = cp.id " +
           "JOIN configuracao_pagamento cfp ON ip.pos_id = cfp.pos_id " +
           "JOIN usuarios u ON cfp.usuario_id = u.id " +
           "WHERE u.id = :usuarioId " +
           "AND (:status IS NULL OR p.status = :status) " +
           "AND (:ambiente IS NULL OR cfp.ambiente = :ambiente) " +
           "ORDER BY created_at DESC",
           countQuery = "SELECT COUNT(*) FROM pagamento p " +
           "JOIN intencao_pagamento ip ON p.intencao_pagamento_id = ip.id " +
           "JOIN conta_pagamento cp ON ip.conta_pagamento_id = cp.id " +
           "JOIN configuracao_pagamento cfp ON ip.pos_id = cfp.pos_id " +
           "JOIN usuarios u ON cfp.usuario_id = u.id " +
           "WHERE u.id = :usuarioId " +
           "AND (:status IS NULL OR p.status = :status) " +
           "AND (:ambiente IS NULL OR cfp.ambiente = :ambiente)",
           nativeQuery = true)
    Page<PagamentoEntity> findByUsuarioIdAndStatusAndAmbienteIfPresent(
            @Param("usuarioId") Long usuarioId,
            @Param("status") String status,
            @Param("ambiente") String ambiente,
            Pageable pageable);

    /**
     * Busca pagamentos por usuário e período de data para geração de gráficos
     * @param usuarioId ID do usuário
     * @param dataInicio Data inicial do período
     * @param dataFim Data final do período
     * @return Lista de pagamentos no período
     */
    @Query(value = "SELECT p.* FROM pagamento p " +
           "JOIN intencao_pagamento ip ON p.intencao_pagamento_id = ip.id " +
           "JOIN conta_pagamento cp ON ip.conta_pagamento_id = cp.id " +
           "JOIN configuracao_pagamento cfp ON ip.pos_id = cfp.pos_id " +
           "JOIN usuarios u ON cfp.usuario_id = u.id " +
           "WHERE u.id = :usuarioId " +
           "AND p.created_at BETWEEN :dataInicio AND :dataFim " +
           "ORDER BY p.created_at",
           nativeQuery = true)
    List<PagamentoEntity> findByUsuarioIdAndDataPagamentoBetween(
            @Param("usuarioId") Long usuarioId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);
}
