package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;

import java.util.List;
import java.util.Optional;

public interface BuscaConfiguracaoPagamentoPort {
    Optional<ConfiguracaoPagamento> buscarPorCredenciais(String apiKey, String apiSecret);
    Optional<ConfiguracaoPagamento> buscarPorApiKey(String apiKey);
    Optional<ConfiguracaoPagamento> buscarPorPosId(String posId);
    List<ConfiguracaoPagamento> buscarTodas();
    Optional<ConfiguracaoPagamento> buscarPorId(Long id);
    
    // Novos métodos para buscar configurações por usuário
    List<ConfiguracaoPagamento> buscarPorUsuario(Long usuarioId);
    List<ConfiguracaoPagamento> buscarAtivasPorUsuario(Long usuarioId);
    Optional<ConfiguracaoPagamento> buscarPorUsuarioEPosId(Long usuarioId, String posId);
    Optional<ConfiguracaoPagamento> buscarPorUsuarioId(Long usuarioId);
    
    // Métodos para buscar por tenant
    List<ConfiguracaoPagamento> buscarPorTenant(Long tenantId);
    List<ConfiguracaoPagamento> buscarPorUsuarioETenant(Long usuarioId, Long tenantId);
}
