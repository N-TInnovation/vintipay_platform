package cv.ntinnovation.gateway_payment.application.core.usecase;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.gateway_payment.application.core.service.CriptografiaService;
import cv.ntinnovation.gateway_payment.application.port.in.CriaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.out.SalvaConfiguracaoPagamentoPort;
import cv.ntinnovation.gateway_payment.application.core.service.ApiSecretGeneratorService;
import cv.ntinnovation.gateway_payment.application.core.service.ApiKeyGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CriaConfiguracaoPagamentoUseCaseImpl implements CriaConfiguracaoPagamentoUseCase {

    private final SalvaConfiguracaoPagamentoPort salvaConfiguracaoPagamentoPort;
    private final CriptografiaService criptografiaService;
    private final ApiSecretGeneratorService apiSecretGeneratorService;
    private final ApiKeyGeneratorService apiKeyGeneratorService;

    @Override
    @Transactional
    public ConfiguracaoPagamento criar(ConfiguracaoPagamento configuracaoPagamento) {
        String apiSecret = apiSecretGeneratorService.generate();
        String apiKey = apiKeyGeneratorService.generate();
        
        ConfiguracaoPagamento configuracaoSalva = salvaConfiguracaoPagamentoPort.salvar(
            ConfiguracaoPagamento.builder()
                .posId(configuracaoPagamento.getPosId())
                .merchantId(configuracaoPagamento.getMerchantId())
                .terminalId(configuracaoPagamento.getTerminalId())
                .apiKey(apiKey)
                .apiSecret(criptografiaService.criptografar(apiSecret))
                .posAutCode(configuracaoPagamento.getPosAutCode())
                .returnUrl(configuracaoPagamento.getReturnUrl())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build()
        );
        
        // Retorna a configuração com o apiSecret e apiKey gerados
        return ConfiguracaoPagamento.builder()
            .id(configuracaoSalva.getId())
            .apiKey(apiKey)
            .apiSecret(apiSecret)
            .build();
    }
}
