package cv.ntinnovation.gateway_payment.adapter.out.db.repository;

import cv.ntinnovation.gateway_payment.adapter.out.db.entity.ConfiguracaoPagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracaoPagamentoRepository extends JpaRepository<ConfiguracaoPagamentoEntity, Long> {
    Optional<ConfiguracaoPagamentoEntity> findByApiKeyAndApiSecret(String apiKey, String apiSecret);
    Optional<ConfiguracaoPagamentoEntity> findByApiKey(String apiKey);
    Optional<ConfiguracaoPagamentoEntity> findByPosId(String posId);
}
