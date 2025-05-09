package cv.ntinnovation.vintipay.adapter.out.db.adapter;

import cv.ntinnovation.vintipay.adapter.out.db.mapper.ConfiguracaoAplicacaoMapper;
import cv.ntinnovation.vintipay.adapter.out.db.repository.ConfiguracaoAplicacaoRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoAplicacao;
import cv.ntinnovation.vintipay.application.port.out.ConfiguracaoAplicacaoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ConfiguracaoAplicacaoAdapter implements ConfiguracaoAplicacaoPort {
    private final ConfiguracaoAplicacaoRepository configuracaoAplicacaoRepository;
    private final ConfiguracaoAplicacaoMapper configuracaoAplicacaoMapper;

    @Override
    public void salvar(ConfiguracaoAplicacao configuracaoAplicacao) {
        var entity = configuracaoAplicacaoMapper.toEntity(configuracaoAplicacao);
        configuracaoAplicacaoRepository.save(entity);
    }

    @Override
    public List<ConfiguracaoAplicacao> buscarTodas(Long configuracaoPagamentoId, Long usuarioId) {

        var list = configuracaoAplicacaoRepository.findByConfiguracaoPagamentoIdAndUsuarioId(configuracaoPagamentoId, usuarioId);

        return list.stream()
                .map(configuracaoAplicacaoMapper::toDomain)
                .collect(Collectors.toList());
    }
}
