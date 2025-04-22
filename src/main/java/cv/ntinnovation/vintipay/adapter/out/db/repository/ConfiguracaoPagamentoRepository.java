package cv.ntinnovation.vintipay.adapter.out.db.repository;

import cv.ntinnovation.vintipay.adapter.out.db.entity.ConfiguracaoPagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracaoPagamentoRepository extends JpaRepository<ConfiguracaoPagamentoEntity, Long> {
    Optional<ConfiguracaoPagamentoEntity> findByApiKeyAndApiSecret(String apiKey, String apiSecret);
    Optional<ConfiguracaoPagamentoEntity> findByApiKey(String apiKey);
    Optional<ConfiguracaoPagamentoEntity> findByPosIdAndIsActiveTrue(String posId);
    Optional<ConfiguracaoPagamentoEntity> findByApiKeyAndIsActiveTrue(String apiKey);
}
