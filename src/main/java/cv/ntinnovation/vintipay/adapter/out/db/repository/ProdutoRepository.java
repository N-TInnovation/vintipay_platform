package cv.ntinnovation.vintipay.adapter.out.db.repository;

import cv.ntinnovation.vintipay.adapter.out.db.entity.ProdutoEntity;
import cv.ntinnovation.vintipay.adapter.out.db.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<ProdutoEntity, Long> {
    
    List<ProdutoEntity> findByProprietarioOrderByDataCriacaoDesc(UsuarioEntity proprietario);
    
    List<ProdutoEntity> findByProprietarioIdOrderByDataCriacaoDesc(Long proprietarioId);
    
    boolean existsByIdAndProprietarioId(Long id, Long proprietarioId);
}
