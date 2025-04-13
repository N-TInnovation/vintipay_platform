package cv.ntinnovation.gateway_payment.application.core.usecase;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.gateway_payment.application.port.in.BuscaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.out.BuscaConfiguracaoPagamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuscaConfiguracaoPagamentoUseCaseImpl implements BuscaConfiguracaoPagamentoUseCase {

    private final BuscaConfiguracaoPagamentoPort buscaConfiguracaoPagamentoPort;

    @Override
    public ConfiguracaoPagamento buscarPorId(Long id) {
        return null;
    }

    @Override
    public ConfiguracaoPagamento buscarPorPosId(String posId) {
        return buscaConfiguracaoPagamentoPort.buscarPorPosId(posId)
                .orElseThrow(() -> new RuntimeException("Configuração de pagamento não encontrada para o POS ID: " + posId));
    }

    @Override
    public List<ConfiguracaoPagamento> buscarTodas() {
        return List.of();
    }
}
