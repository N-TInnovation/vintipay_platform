package cv.ntinnovation.vintipay.adapter.out.persistence.repository;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.ProdutoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<ProdutoEntity, Long> {
    
    List<ProdutoEntity> findByProprietarioIdOrderByDataCriacaoDesc(Long proprietarioId);
    
    List<ProdutoEntity> findByAtivoTrue();
    
    List<ProdutoEntity> findByAtivoTrueAndProprietarioId(Long proprietarioId);
    
    List<ProdutoEntity> findByCategoria(String categoria);
    
    boolean existsByIdAndProprietarioId(Long id, Long proprietarioId);
}
