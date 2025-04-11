package cv.ntinnovation.gateway_payment.adapter.out.db.adapter;

import cv.ntinnovation.gateway_payment.adapter.out.db.mapper.ConfiguracaoPagamentoMapper;
import cv.ntinnovation.gateway_payment.adapter.out.db.repository.ConfiguracaoPagamentoRepository;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.gateway_payment.application.core.service.CriptografiaService;
import cv.ntinnovation.gateway_payment.application.port.out.BuscaConfiguracaoPagamentoPort;
import cv.ntinnovation.gateway_payment.application.port.out.SalvaConfiguracaoPagamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConfiguracaoPagamentoAdapter implements BuscaConfiguracaoPagamentoPort, SalvaConfiguracaoPagamentoPort {

    private final ConfiguracaoPagamentoRepository configuracaoPagamentoRepository;
    private final ConfiguracaoPagamentoMapper configuracaoPagamentoMapper;
    private final CriptografiaService criptografiaService;

    @Override
    public Optional<ConfiguracaoPagamento> buscarPorCredenciais(String clientId, String clientSecret) {
        return configuracaoPagamentoRepository.findByApiKeyAndIsActiveTrue(clientId)
                .filter(config -> criptografiaService.verificar(clientSecret, config.getApiSecret()))
                .map(configuracaoPagamentoMapper::toConfiguracao);
    }

    @Override
    public Optional<ConfiguracaoPagamento> buscarPorApiKey(String apiKey) {
        return configuracaoPagamentoRepository.findByApiKeyAndIsActiveTrue(apiKey)
                .map(configuracaoPagamentoMapper::toConfiguracao);
    }

    @Override
    public ConfiguracaoPagamento salvar(ConfiguracaoPagamento configuracaoPagamento) {
        var entity = configuracaoPagamentoMapper.toEntity(configuracaoPagamento);
        var savedEntity = configuracaoPagamentoRepository.save(entity);
        return configuracaoPagamentoMapper.toConfiguracao(savedEntity);
    }
}
