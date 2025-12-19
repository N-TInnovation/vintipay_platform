package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.DataFingerPrint;
import cv.ntinnovation.vintipay.application.core.domain.entity.FingerPrintResult;
import cv.ntinnovation.vintipay.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.SubscricaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.AmbienteEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.IntencaoPagamentoEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.MoedaEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusSubscricaoPagamentoEnum;
import cv.ntinnovation.vintipay.application.core.domain.rules.FingerPrintService;
import cv.ntinnovation.vintipay.application.port.in.BuscaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.CriaSubscricaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.out.SalvaSubscricaoPagamentoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CriaSubscricaoPagamentoUseCaseImpl implements CriaSubscricaoPagamentoUseCase {
    
    private final SalvaSubscricaoPagamentoPort salvaSubscricaoPagamentoPort;
    private final BuscaConfiguracaoPagamentoUseCase buscaConfiguracaoPagamentoUseCase;
    private final FingerPrintService fingerPrintService;

    @Override
    public SubscricaoPagamento subscrever(IntencaoPagamento intencaoPagamento) {
        log.info("Criando subscrição para intenção de pagamento ID: {}", intencaoPagamento.getId());
        
        // Verificar se já existe uma subscrição para esta intenção de pagamento
        var existenteOpt = salvaSubscricaoPagamentoPort.buscarPorIntencaoPagamentoId(intencaoPagamento.getId());
        if (existenteOpt.isPresent()) {
            log.warn("Já existe uma subscrição para esta intenção de pagamento ID: {}", intencaoPagamento.getId());
            return existenteOpt.get();
        }

        // Buscar configuração de pagamento pelo posId da intenção
        var configuracaoPagamento = buscaConfiguracaoPagamentoUseCase.buscarPorPosId(intencaoPagamento.getPosId());

        // Criar uma nova subscrição
        var subscricao = SubscricaoPagamento.builder()
                .intencaoPagamento(intencaoPagamento)
                .reference(generateUniqueReference())
                .status(StatusSubscricaoPagamentoEnum.INSCRICAO_PENDENTE)
                .createdAt(LocalDateTime.now())
                .ciclo(1) // Começando com o primeiro ciclo
                .ambiente(intencaoPagamento.getAmbiente()) // Ou obter de configurações
                .tenantId(intencaoPagamento.getTenantId())
                .tenant(intencaoPagamento.getTenant())
                .build();

        // Gerar fingerprint usando o FingerPrintService
        DataFingerPrint dataFingerPrint = DataFingerPrint.builder()
                .posAutCode(configuracaoPagamento.getPosAutCode())
                .timestamp(intencaoPagamento.getTimestamp())
                .amount(intencaoPagamento.getValor())
                .merchantRef(intencaoPagamento.getMerchantRef())
                .merchantSession(intencaoPagamento.getMerchantSession())
                .posID(intencaoPagamento.getPosId())
                .currency(MoedaEnum.getByName(intencaoPagamento.getMoeda()).getCodigoISO())
                .transactionCode(IntencaoPagamentoEnum.SUBSCRICAO.getTransactionCode())
                .entityCode("")
                .referenceNumber("")
                .build();

        FingerPrintResult fingerPrintResult = fingerPrintService.generateFingerPrint(dataFingerPrint);
        subscricao.setFingerprint(fingerPrintResult.getHashFingerPrint());

        // Salvar no banco de dados
        var savedSubscricao = salvaSubscricaoPagamentoPort.salvar(subscricao);
        
        log.info("Subscrição criada com sucesso. ID: {}, Referência: {}", savedSubscricao.getId(), savedSubscricao.getReference());
        
        return savedSubscricao;
    }
    
    /**
     * Gera uma referência única para a subscrição
     */
    private String generateUniqueReference() {
        return "SUB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
