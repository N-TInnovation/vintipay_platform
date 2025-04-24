package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.rules.FingerPrintService;
import cv.ntinnovation.vintipay.application.core.service.CriptografiaService;
import cv.ntinnovation.vintipay.application.port.in.CriaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.out.SalvaConfiguracaoPagamentoPort;
import cv.ntinnovation.vintipay.application.port.out.ConsultaConfiguracaoPagamentoPort;
import cv.ntinnovation.vintipay.application.core.service.ApiSecretGeneratorService;
import cv.ntinnovation.vintipay.application.core.service.ApiKeyGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
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
    private final ConsultaConfiguracaoPagamentoPort consultaConfiguracaoPagamentoPort;

    @Override
    @Transactional
    public ConfiguracaoPagamento criar(ConfiguracaoPagamento configuracaoPagamento) {
        log.info("Criando nova configuração de pagamento: {}", configuracaoPagamento);
        
        try {
            // Verificar explicitamente se o POS ID já existe
            if (configuracaoPagamento.getPosId() != null && 
                consultaConfiguracaoPagamentoPort.existePorPosId(configuracaoPagamento.getPosId())) {
                throw new IllegalArgumentException("Já existe uma configuração com este POS ID: " + configuracaoPagamento.getPosId());
            }
            
            // Validar se o usuário foi informado
            if (configuracaoPagamento.getUsuarioId() == null) {
                throw new IllegalArgumentException("O ID do usuário é obrigatório para criar uma configuração de pagamento");
            }
            
            String apiSecret = apiSecretGeneratorService.generate();
            String apiKey = apiKeyGeneratorService.generate();
            
            // Gerar returnAutCode se não foi fornecido
            String returnAutCode = configuracaoPagamento.getReturnAutCode();
            if (returnAutCode == null || returnAutCode.isEmpty()) {
                returnAutCode = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
                log.info("ReturnAutCode gerado automaticamente: {}", returnAutCode);
            }
            
            // Armazenar o POS Aut Code original para retornar ao usuário
            String posAutCodeOriginal = configuracaoPagamento.getPosAutCode();
            
            // Criptografar o POS Aut Code com AES256 usando o FingerPrintService
            String posAutCodeCriptografado = fingerPrintService.encryptAES256(posAutCodeOriginal);
            log.info("POS Aut Code criptografado com AES256");
            
            // Criar um objeto de configuração para salvar
            ConfiguracaoPagamento configParaSalvar = ConfiguracaoPagamento.builder()
                .posId(configuracaoPagamento.getPosId())
                .merchantId(configuracaoPagamento.getMerchantId())
                .terminalId(configuracaoPagamento.getTerminalId())
                .apiKey(apiKey)
                .apiSecret(criptografiaService.criptografar(apiSecret))
                .posAutCode(posAutCodeCriptografado) // Salvar o valor criptografado
                .returnAutCode(returnAutCode)
                .returnUrl(configuracaoPagamento.getReturnUrl())
                .nomeLoja(configuracaoPagamento.getNomeLoja())
                .urlLoja(configuracaoPagamento.getUrlLoja())
                .telefone(configuracaoPagamento.getTelefone())
                .email(configuracaoPagamento.getEmail())
                .usuarioId(configuracaoPagamento.getUsuarioId())
                .ambiente(configuracaoPagamento.getAmbiente())
                .serviceUrl(configuracaoPagamento.getServiceUrl())
                .webhookUrl(configuracaoPagamento.getWebhookUrl())
                .webhookSecret(configuracaoPagamento.getWebhookSecret())
                .ativo(configuracaoPagamento.getAtivo() != null ? configuracaoPagamento.getAtivo() : true)
                .observacoes(configuracaoPagamento.getObservacoes())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
            
            // Não definir o objeto Usuario, apenas o ID
            
            ConfiguracaoPagamento configuracaoSalva = salvaConfiguracaoPagamentoPort.salvar(configParaSalvar);
            
            // Retorna a configuração com o apiSecret, apiKey e posAutCode original
            return ConfiguracaoPagamento.builder()
                .id(configuracaoSalva.getId())
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .posAutCode(posAutCodeOriginal) // Retornar o valor original
                .returnAutCode(returnAutCode)
                .usuarioId(configuracaoPagamento.getUsuarioId())
                .build();
        } catch (DataIntegrityViolationException e) {
            // Verificar se é um erro de duplicação de POS ID
            if (e.getMessage() != null && e.getMessage().contains("Duplicate entry") && 
                e.getMessage().contains("UKiq9p0eeolwpce4njfkxd6vhpn")) {
                log.error("Tentativa de criar configuração com POS ID duplicado: {}", configuracaoPagamento.getPosId());
                throw new IllegalArgumentException("Já existe uma configuração com este POS ID: " + configuracaoPagamento.getPosId(), e);
            }
            // Outros erros de integridade de dados
            log.error("Erro de integridade de dados ao criar configuração", e);
            throw e;
        } catch (Exception e) {
            log.error("Erro ao criar configuração de pagamento", e);
            throw e;
        }
    }
}
