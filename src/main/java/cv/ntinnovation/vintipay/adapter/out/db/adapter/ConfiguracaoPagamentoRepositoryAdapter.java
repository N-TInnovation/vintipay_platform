package cv.ntinnovation.vintipay.adapter.out.db.adapter;

import cv.ntinnovation.vintipay.adapter.out.db.entity.ConfiguracaoPagamentoEntity;
import cv.ntinnovation.vintipay.adapter.out.db.mapper.ConfiguracaoPagamentoMapper;
import cv.ntinnovation.vintipay.adapter.out.db.repository.ConfiguracaoPagamentoRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.port.out.BuscaConfiguracaoPagamento;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Primary
@Component
@RequiredArgsConstructor
public class ConfiguracaoPagamentoRepositoryAdapter implements BuscaConfiguracaoPagamento {

    private final ConfiguracaoPagamentoRepository configuracaoPagamentoRepository;
    private final ConfiguracaoPagamentoMapper configuracaoPagamentoMapper;

    @Override
    public Optional<ConfiguracaoPagamento> buscarPorId(Long id) {
        return configuracaoPagamentoRepository.findById(id)
                .map(configuracaoPagamentoMapper::toDomain);
    }

    @Override
    public List<ConfiguracaoPagamento> buscarPorUsuarioId(Long usuarioId) {
        return configuracaoPagamentoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(configuracaoPagamentoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConfiguracaoPagamento> buscarAtivasPorUsuarioId(Long usuarioId) {
        return configuracaoPagamentoRepository.findByUsuarioIdAndIsActiveTrue(usuarioId)
                .stream()
                .map(configuracaoPagamentoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existePorIdEUsuarioId(Long id, Long usuarioId) {
        Optional<ConfiguracaoPagamentoEntity> configuracao = configuracaoPagamentoRepository.findById(id);
        return configuracao.isPresent() && configuracao.get().getUsuario() != null 
                && configuracao.get().getUsuario().getId().equals(usuarioId);
    }
}
