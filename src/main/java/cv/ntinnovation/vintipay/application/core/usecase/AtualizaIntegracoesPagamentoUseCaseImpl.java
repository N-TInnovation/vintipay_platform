package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoIntegracoes;
import cv.ntinnovation.vintipay.application.port.in.AtualizaIntegracoesPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.out.ConfiguracaoIntegracoesPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@RequiredArgsConstructor
@Service
public class AtualizaIntegracoesPagamentoUseCaseImpl implements AtualizaIntegracoesPagamentoUseCase {

    private final ConfiguracaoIntegracoesPort configuracaoIntegracoesPort;

    @Override
    public ConfiguracaoIntegracoes atualizar(ConfiguracaoIntegracoes configuracaoIntegracoes) {
        return configuracaoIntegracoesPort.salvar(configuracaoIntegracoes);
    }

    @Override
    public void ativar(Long id) {
        var configuracaoAplicacao = configuracaoIntegracoesPort.buscarPorId(id);
        configuracaoAplicacao.setActive(true);
        configuracaoAplicacao.setUpdatedAt(LocalDateTime.now());
        configuracaoIntegracoesPort.salvar(configuracaoAplicacao);
    }

    @Override
    public void desativar(Long id) {
        var configuracaoAplicacao = configuracaoIntegracoesPort.buscarPorId(id);
        configuracaoAplicacao.setActive(false);
        configuracaoAplicacao.setUpdatedAt(LocalDateTime.now());
        configuracaoIntegracoesPort.salvar(configuracaoAplicacao);
    }

    @Override
    public boolean deletarPorIdUsuario(Long id, Long usuarioId) {
        return configuracaoIntegracoesPort.deletarPorIdUsuario(id, usuarioId);
    }
}
