package cv.ntinnovation.vintipay.adapter.out.db.repository;

import cv.ntinnovation.vintipay.adapter.out.db.entity.ConfiguracaoPagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    // Método para verificar se existe configuração com o POS ID fornecido
    boolean existsByPosId(String posId);

    // Método para verificar se existe configuração com o POS ID para um usuário específico
    boolean existsByPosIdAndUsuarioId(String posId, Long usuarioId);

    // Método para verificar se existe configuração com o POS ID para outros usuários
    boolean existsByPosIdAndUsuarioIdNot(String posId, Long usuarioId);

    // Método para buscar configuração por POS ID (para depuração)
    Optional<ConfiguracaoPagamentoEntity> findByPosId(String posId);
}
