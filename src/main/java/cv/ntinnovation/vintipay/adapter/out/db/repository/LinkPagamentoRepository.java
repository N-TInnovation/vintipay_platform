package cv.ntinnovation.vintipay.adapter.out.db.repository;

import cv.ntinnovation.vintipay.adapter.out.db.entity.LinkPagamentoEntity;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusLinkPagamentoEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LinkPagamentoRepository extends JpaRepository<LinkPagamentoEntity, Long> {
    
    Optional<LinkPagamentoEntity> findByCodigo(String codigo);
    
    List<LinkPagamentoEntity> findByProprietarioIdOrderByDataCriacaoDesc(Long proprietarioId);
    
    List<LinkPagamentoEntity> findByProdutoIdOrderByDataCriacaoDesc(Long produtoId);
    
    List<LinkPagamentoEntity> findByStatus(StatusLinkPagamentoEnum status);
    
    List<LinkPagamentoEntity> findByStatusAndProprietarioId(StatusLinkPagamentoEnum status, Long proprietarioId);
    
    boolean existsByIdAndProprietarioId(Long id, Long proprietarioId);
}
