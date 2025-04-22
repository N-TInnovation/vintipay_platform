package cv.ntinnovation.vintipay.adapter.out.db.repository;

import cv.ntinnovation.vintipay.adapter.out.db.entity.ConfiguracaoPagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfiguracaoPagamentoRepository extends JpaRepository<ConfiguracaoPagamentoEntity, Long> {
    Optional<ConfiguracaoPagamentoEntity> findByApiKeyAndApiSecret(String apiKey, String apiSecret);
    Optional<ConfiguracaoPagamentoEntity> findByApiKey(String apiKey);
    Optional<ConfiguracaoPagamentoEntity> findByPosIdAndIsActiveTrue(String posId);
    Optional<ConfiguracaoPagamentoEntity> findByApiKeyAndIsActiveTrue(String apiKey);
    
    // Novos métodos para buscar configurações por usuário
    List<ConfiguracaoPagamentoEntity> findByUsuarioId(Long usuarioId);
    List<ConfiguracaoPagamentoEntity> findByUsuarioIdAndIsActiveTrue(Long usuarioId);
    Optional<ConfiguracaoPagamentoEntity> findByUsuarioIdAndPosId(Long usuarioId, String posId);
}
