package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.rules.FingerPrintService;
import cv.ntinnovation.vintipay.application.port.in.BuscaConfiguracaoPagamentoAtivaUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaConfiguracaoPagamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscaConfiguracaoPagamentoAtivaUseCaseImpl implements BuscaConfiguracaoPagamentoAtivaUseCase {

    private final BuscaConfiguracaoPagamentoPort buscaConfiguracaoPagamentoPort;
    private final FingerPrintService fingerPrintService;

    @Override
    public ConfiguracaoPagamento buscarPorCredenciais(String clientId, String clientSecret) {
        return buscaConfiguracaoPagamentoPort.buscarPorCredenciais(clientId, clientSecret)
                .orElseThrow(() -> new IllegalStateException("Credenciais inválidas"));
    }

    @Override
    public ConfiguracaoPagamento buscarPorClientId(String clientId) {
        ConfiguracaoPagamento configuracaoPagamento = buscaConfiguracaoPagamentoPort.buscarPorApiKey(clientId)
                .orElseThrow(() -> new IllegalStateException("Configuração não encontrada ou inativa"));
        return descriptografarPosAutCode(configuracaoPagamento);
    }

    /**
     * Descriptografa o campo posAutCode da configuração
     * @param configuracao Configuração com posAutCode criptografado
     * @return Configuração com posAutCode descriptografado
     */
    private ConfiguracaoPagamento descriptografarPosAutCode(ConfiguracaoPagamento configuracao) {
        if (configuracao.getPosAutCode() != null) {
            String posAutCodeDescriptografado = fingerPrintService.decryptAES256(configuracao.getPosAutCode());

            configuracao.setPosAutCode(posAutCodeDescriptografado);
        }
        return configuracao;
    }
}
