package cv.ntinnovation.vintipay.adapter.out.db.adapter;

import cv.ntinnovation.vintipay.adapter.out.db.entity.ConfiguracaoPagamentoEntity;
import cv.ntinnovation.vintipay.adapter.out.db.mapper.ConfiguracaoPagamentoMapper;
import cv.ntinnovation.vintipay.adapter.out.db.repository.ConfiguracaoPagamentoRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.service.CriptografiaService;
import cv.ntinnovation.vintipay.application.port.out.BuscaConfiguracaoPagamentoPort;
import cv.ntinnovation.vintipay.application.port.out.SalvaConfiguracaoPagamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ConfiguracaoPagamentoAdapter implements BuscaConfiguracaoPagamentoPort, SalvaConfiguracaoPagamentoPort {

    private final ConfiguracaoPagamentoRepository repository;
    private final ConfiguracaoPagamentoMapper mapper;
    private final CriptografiaService criptografiaService;

    @Override
    public Optional<ConfiguracaoPagamento> buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<ConfiguracaoPagamento> buscarPorCredenciais(String apiKey, String apiSecret) {
        return repository.findByApiKeyAndIsActiveTrue(apiKey)
                .filter(config -> criptografiaService.verificar(apiSecret, config.getApiSecret()))
                .map(mapper::toDomain);
    }

    @Override
    public Optional<ConfiguracaoPagamento> buscarPorApiKey(String clientId) {
        return repository.findByApiKeyAndIsActiveTrue(clientId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<ConfiguracaoPagamento> buscarPorPosId(String posId) {
        return repository.findByPosIdAndIsActiveTrue(posId)
                .map(mapper::toDomain);
    }

    @Override
    public List<ConfiguracaoPagamento> buscarTodas() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public ConfiguracaoPagamento salvar(ConfiguracaoPagamento configuracao) {
        ConfiguracaoPagamentoEntity entity = mapper.toEntity(configuracao);
        return mapper.toDomain(repository.save(entity));
    }
}
