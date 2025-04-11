package cv.ntinnovation.gateway_payment.application.core.usecase;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.gateway_payment.application.port.in.BuscaConfiguracaoPagamentoAtivaUseCase;
import cv.ntinnovation.gateway_payment.application.port.out.BuscaConfiguracaoPagamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscaConfiguracaoPagamentoAtivaUseCaseImpl implements BuscaConfiguracaoPagamentoAtivaUseCase {

    private final BuscaConfiguracaoPagamentoPort buscaConfiguracaoPagamentoPort;

    @Override
    public ConfiguracaoPagamento buscarPorCredenciais(String clientId, String clientSecret) {
        return buscaConfiguracaoPagamentoPort.buscarPorCredenciais(clientId, clientSecret)
                .orElseThrow(() -> new IllegalStateException("Credenciais inválidas"));
    }

    @Override
    public ConfiguracaoPagamento buscarPorClientId(String clientId) {
        return buscaConfiguracaoPagamentoPort.buscarPorApiKey(clientId)
                .orElseThrow(() -> new IllegalStateException("Configuração não encontrada ou inativa"));
    }
}
