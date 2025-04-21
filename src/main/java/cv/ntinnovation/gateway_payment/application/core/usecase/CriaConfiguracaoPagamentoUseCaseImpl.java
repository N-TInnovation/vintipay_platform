package cv.ntinnovation.gateway_payment.application.core.usecase;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.rules.FingerPrintService;
import cv.ntinnovation.gateway_payment.application.core.service.CriptografiaService;
import cv.ntinnovation.gateway_payment.application.port.in.CriaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.out.SalvaConfiguracaoPagamentoPort;
import cv.ntinnovation.gateway_payment.application.core.service.ApiSecretGeneratorService;
import cv.ntinnovation.gateway_payment.application.core.service.ApiKeyGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CriaConfiguracaoPagamentoUseCaseImpl implements CriaConfiguracaoPagamentoUseCase {

    private final SalvaConfiguracaoPagamentoPort salvaConfiguracaoPagamentoPort;
    private final CriptografiaService criptografiaService;
    private final ApiSecretGeneratorService apiSecretGeneratorService;
    private final ApiKeyGeneratorService apiKeyGeneratorService;
    private final FingerPrintService fingerPrintService;

    @Override
    @Transactional
    public ConfiguracaoPagamento criar(ConfiguracaoPagamento configuracaoPagamento) {
        String apiSecret = apiSecretGeneratorService.generate();
        String apiKey = apiKeyGeneratorService.generate();
        
        // Armazenar o POS Aut Code original para retornar ao usuário
        String posAutCodeOriginal = configuracaoPagamento.getPosAutCode();
        
        // Criptografar o POS Aut Code com AES256 usando o FingerPrintService
        String posAutCodeCriptografado = fingerPrintService.encryptAES256(posAutCodeOriginal);
        log.info("POS Aut Code criptografado com AES256");
        
        ConfiguracaoPagamento configuracaoSalva = salvaConfiguracaoPagamentoPort.salvar(
            ConfiguracaoPagamento.builder()
                .posId(configuracaoPagamento.getPosId())
                .merchantId(configuracaoPagamento.getMerchantId())
                .terminalId(configuracaoPagamento.getTerminalId())
                .apiKey(apiKey)
                .apiSecret(criptografiaService.criptografar(apiSecret))
                .posAutCode(posAutCodeCriptografado) // Salvar o valor criptografado
                .returnAutCode(configuracaoPagamento.getReturnAutCode())
                .returnUrl(configuracaoPagamento.getReturnUrl())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build()
        );
        
        // Retorna a configuração com o apiSecret, apiKey e posAutCode original
        return ConfiguracaoPagamento.builder()
            .id(configuracaoSalva.getId())
            .apiKey(apiKey)
            .apiSecret(apiSecret)
            .posAutCode(posAutCodeOriginal) // Retornar o valor original
            .returnAutCode(configuracaoPagamento.getReturnAutCode())
            .build();
    }
}
