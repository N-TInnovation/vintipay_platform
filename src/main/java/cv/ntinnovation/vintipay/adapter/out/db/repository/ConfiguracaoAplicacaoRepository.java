package cv.ntinnovation.vintipay.adapter.out.db.repository;

import cv.ntinnovation.vintipay.adapter.out.db.entity.ConfiguracaoAplicacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfiguracaoAplicacaoRepository extends JpaRepository<ConfiguracaoAplicacaoEntity, Long> {
       List<ConfiguracaoAplicacaoEntity> findByConfiguracaoPagamentoIdAndUsuarioId(Long configuracaoPagamentoId, Long usuarioId);
}
