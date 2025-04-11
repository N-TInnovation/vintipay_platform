package cv.ntinnovation.gateway_payment.adapter.out.db.adapter;

import cv.ntinnovation.gateway_payment.adapter.out.db.mapper.ConfiguracaoPagamentoMapper;
import cv.ntinnovation.gateway_payment.adapter.out.db.repository.ConfiguracaoPagamentoRepository;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.gateway_payment.application.port.out.BuscaConfiguracaoPagamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BuscaConfiguracaoPagamentoAdapter implements BuscaConfiguracaoPagamentoPort {

    private final ConfiguracaoPagamentoRepository configuracaoPagamentoRepository;
    private final ConfiguracaoPagamentoMapper configuracaoPagamentoMapper;

    @Override
    public Optional<ConfiguracaoPagamento> buscarPorCredenciais(String apiKey, String apiSecret) {
        return configuracaoPagamentoRepository.findByApiKeyAndApiSecret(apiKey, apiSecret)
                .map(configuracaoPagamentoMapper::toConfiguracao);
    }

    @Override
    public Optional<ConfiguracaoPagamento> buscarPorApiKey(String apiKey) {
        return configuracaoPagamentoRepository.findByApiKey(apiKey)
                .map(configuracaoPagamentoMapper::toConfiguracao);
    }

    @Override
    public Optional<ConfiguracaoPagamento> buscarPorPosId(String posId) {
        return configuracaoPagamentoRepository.findByPosId(posId)
                .map(configuracaoPagamentoMapper::toConfiguracao);
    }
}
