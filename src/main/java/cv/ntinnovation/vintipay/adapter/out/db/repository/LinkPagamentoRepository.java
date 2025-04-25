package cv.ntinnovation.vintipay.adapter.out.db.repository;

import cv.ntinnovation.vintipay.adapter.out.db.entity.LinkPagamentoEntity;
import cv.ntinnovation.vintipay.adapter.out.db.entity.ProdutoEntity;
import cv.ntinnovation.vintipay.adapter.out.db.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LinkPagamentoRepository extends JpaRepository<LinkPagamentoEntity, Long> {
    
    Optional<LinkPagamentoEntity> findByCodigo(String codigo);
    
    List<LinkPagamentoEntity> findByProprietarioOrderByDataCriacaoDesc(UsuarioEntity proprietario);
    
    List<LinkPagamentoEntity> findByProprietarioIdOrderByDataCriacaoDesc(Long proprietarioId);
    
    List<LinkPagamentoEntity> findByProdutoOrderByDataCriacaoDesc(ProdutoEntity produto);
    
    List<LinkPagamentoEntity> findByProdutoIdOrderByDataCriacaoDesc(Long produtoId);
    
    boolean existsByIdAndProprietarioId(Long id, Long proprietarioId);
}
