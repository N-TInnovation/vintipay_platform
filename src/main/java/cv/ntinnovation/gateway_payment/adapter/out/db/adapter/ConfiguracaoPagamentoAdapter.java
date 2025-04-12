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
    public Optional<ConfiguracaoPagamento> buscarPorCredenciais(String apiKey, String apiSecret) {
        return configuracaoPagamentoRepository.findByApiKey(apiKey)
                .filter(config -> criptografiaService.verificar(apiSecret, config.getApiSecret()))
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

    @Override
    public ConfiguracaoPagamento salvar(ConfiguracaoPagamento configuracaoPagamento) {
        var entity = configuracaoPagamentoMapper.toEntity(configuracaoPagamento);
        var savedEntity = configuracaoPagamentoRepository.save(entity);
        return configuracaoPagamentoMapper.toConfiguracao(savedEntity);
    }
}
