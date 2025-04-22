package cv.ntinnovation.vintipay.adapter.out.db.repository;

import cv.ntinnovation.vintipay.adapter.out.db.entity.PagamentoEntity;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
