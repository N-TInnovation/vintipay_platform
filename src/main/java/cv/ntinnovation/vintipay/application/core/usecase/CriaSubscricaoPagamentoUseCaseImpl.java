package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.SubscricaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.AmbienteEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusSubscricaoPagamentoEnum;
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

    @Override
    public SubscricaoPagamento subscrever(IntencaoPagamento intencaoPagamento) {
        log.info("Criando subscrição para intenção de pagamento ID: {}", intencaoPagamento.getId());
        
        // Verificar se já existe uma subscrição para esta intenção de pagamento
        var existenteOpt = salvaSubscricaoPagamentoPort.buscarPorIntencaoPagamentoId(intencaoPagamento.getId());
        if (existenteOpt.isPresent()) {
            log.warn("Já existe uma subscrição para esta intenção de pagamento ID: {}", intencaoPagamento.getId());
            return existenteOpt.get();
        }
        
        // Criar uma nova subscrição
        var subscricao = SubscricaoPagamento.builder()
                .intencaoPagamento(intencaoPagamento)
                .reference(generateUniqueReference())
                .status(StatusSubscricaoPagamentoEnum.INSCRICAO_PENDENTE)
                .createdAt(LocalDateTime.now())
                .ciclo(1) // Começando com o primeiro ciclo
                .ambiente(AmbienteEnum.PRODUCAO) // Ou obter de configurações
                .build();
        
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
