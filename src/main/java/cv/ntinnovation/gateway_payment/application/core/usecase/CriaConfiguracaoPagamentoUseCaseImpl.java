package cv.ntinnovation.gateway_payment.application.core.usecase;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.gateway_payment.application.core.service.CriptografiaService;
import cv.ntinnovation.gateway_payment.application.port.in.CriaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.out.SalvaConfiguracaoPagamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CriaConfiguracaoPagamentoUseCaseImpl implements CriaConfiguracaoPagamentoUseCase {

    private final SalvaConfiguracaoPagamentoPort salvaConfiguracaoPagamentoPort;
    private final CriptografiaService criptografiaService;

    @Override
    @Transactional
    public ConfiguracaoPagamento criar(ConfiguracaoPagamento configuracaoPagamento) {
        return salvaConfiguracaoPagamentoPort.salvar(
            ConfiguracaoPagamento.builder()
                .posId(configuracaoPagamento.getPosId())
                .merchantId(configuracaoPagamento.getMerchantId())
                .terminalId(configuracaoPagamento.getTerminalId())
                .apiKey(configuracaoPagamento.getApiKey())
                .apiSecret(criptografiaService.criptografar(configuracaoPagamento.getApiSecret()))
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build()
        );
    }
}
