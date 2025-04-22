package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.rules.FingerPrintService;
import cv.ntinnovation.vintipay.application.port.in.BuscaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaConfiguracaoPagamentoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuscaConfiguracaoPagamentoUseCaseImpl implements BuscaConfiguracaoPagamentoUseCase {

    private final BuscaConfiguracaoPagamentoPort buscaConfiguracaoPagamentoPort;
    private final FingerPrintService fingerPrintService;

    @Override
    public ConfiguracaoPagamento buscarPorId(Long id) {
        ConfiguracaoPagamento configuracao = buscaConfiguracaoPagamentoPort.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Configuração de pagamento não encontrada para o ID: " + id));
        
        return descriptografarPosAutCode(configuracao);
    }

    @Override
    public ConfiguracaoPagamento buscarPorPosId(String posId) {
        ConfiguracaoPagamento configuracao = buscaConfiguracaoPagamentoPort.buscarPorPosId(posId)
                .orElseThrow(() -> new RuntimeException("Configuração de pagamento não encontrada para o POS ID: " + posId));
        
        return descriptografarPosAutCode(configuracao);
    }

    @Override
    public List<ConfiguracaoPagamento> buscarTodas() {
        List<ConfiguracaoPagamento> configuracoes = buscaConfiguracaoPagamentoPort.buscarTodas();
        
        return configuracoes.stream()
                .map(this::descriptografarPosAutCode)
                .collect(Collectors.toList());
    }
    
    /**
     * Descriptografa o campo posAutCode da configuração
     * @param configuracao Configuração com posAutCode criptografado
     * @return Configuração com posAutCode descriptografado
     */
    private ConfiguracaoPagamento descriptografarPosAutCode(ConfiguracaoPagamento configuracao) {
        try {
            if (configuracao.getPosAutCode() != null) {
                String posAutCodeDescriptografado = fingerPrintService.decryptAES256(configuracao.getPosAutCode());
                
                // Criar uma nova instância com o posAutCode descriptografado
                return ConfiguracaoPagamento.builder()
                        .id(configuracao.getId())
                        .posId(configuracao.getPosId())
                        .merchantId(configuracao.getMerchantId())
                        .terminalId(configuracao.getTerminalId())
                        .apiKey(configuracao.getApiKey())
                        .apiSecret(configuracao.getApiSecret())
                        .posAutCode(posAutCodeDescriptografado)
                        .returnAutCode(configuracao.getReturnAutCode())
                        .returnUrl(configuracao.getReturnUrl())
                        .isActive(configuracao.getIsActive())
                        .nomeLoja(configuracao.getNomeLoja())
                        .urlLoja(configuracao.getUrlLoja())
                        .telefone(configuracao.getTelefone())
                        .email(configuracao.getEmail())
                        .createdAt(configuracao.getCreatedAt())
                        .updatedAt(configuracao.getUpdatedAt())
                        .build();
            }
        } catch (Exception e) {
            log.error("Erro ao descriptografar posAutCode: {}", e.getMessage(), e);
            // Em caso de erro, retornar a configuração original
        }
        
        return configuracao;
    }
}
