package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoIntegracoes;
import cv.ntinnovation.vintipay.application.port.in.BuscaIntegracoesUseCase;
import cv.ntinnovation.vintipay.application.port.out.ConfiguracaoIntegracoesPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuscaIntegracoesUseCaseImpl implements BuscaIntegracoesUseCase {

    private final ConfiguracaoIntegracoesPort configuracaoIntegracoesPort;
    
    @Override
    public List<ConfiguracaoIntegracoes> buscarIntegracoesAtivasPorUsuario(Long usuarioId) {
        log.info("Buscando integrações ativas para o usuário ID: {}", usuarioId);
        List<ConfiguracaoIntegracoes> integracoes = configuracaoIntegracoesPort.buscarAtivasPorUsuario(usuarioId);
        log.info("Encontradas {} integrações ativas para o usuário", integracoes.size());
        return integracoes;
    }

    @Override
    public List<ConfiguracaoIntegracoes> buscarTodasIntegracoesPorIdUsuario(Long usuarioId) {
        return configuracaoIntegracoesPort.buscarTodas(usuarioId);
    }

    @Override
    public Optional<ConfiguracaoIntegracoes> buscarIntegracaoPorIdUsuario(Long id, Long usuarioId) {
        return configuracaoIntegracoesPort.buscarPorIdUsuario(id, usuarioId);
    }

    @Override
    public boolean existeTipoIntegracaoEmConfiguracaoPagamentoPorUsuario(Long id, String tipoIntegracao, Long usuarioId) {
        return configuracaoIntegracoesPort.existeTipoIntegracaoEmConfiguracaoPagamentoPorUsuario(id, tipoIntegracao, usuarioId);
    }
}
