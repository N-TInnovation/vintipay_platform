package cv.ntinnovation.gateway_payment.application.core.usecase;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.enums.StatusPagamentoEnum;
import cv.ntinnovation.gateway_payment.application.port.in.CriaIntencaoPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.out.SalvaIntencaoPagamento;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CriaIntencaoPagamentoUseCaseImpl implements CriaIntencaoPagamentoUseCase {

    private final SalvaIntencaoPagamento salvaIntencaoPagamento;

    public CriaIntencaoPagamentoUseCaseImpl(SalvaIntencaoPagamento salvaIntencaoPagamento) {
        this.salvaIntencaoPagamento = salvaIntencaoPagamento;
    }

    @Override
    public IntencaoPagamento criar(IntencaoPagamento intencaoPagamento) {
        // Definir valores padrão
        if (intencaoPagamento.getCreatedAt() == null) {
            intencaoPagamento.setCreatedAt(LocalDateTime.now());
        }
        
        if (intencaoPagamento.getReference() == null || intencaoPagamento.getReference().isEmpty()) {
            intencaoPagamento.setReference(UUID.randomUUID().toString());
        }
        
        // Definir status inicial
        if (intencaoPagamento.getStatus() == null) {
            intencaoPagamento.setStatus(StatusPagamentoEnum.PENDENTE);
        }
        
        // Validar dados obrigatórios
        validarIntencaoPagamento(intencaoPagamento);
        
        // Salvar a intenção de pagamento
        return salvaIntencaoPagamento.salvar(intencaoPagamento);
    }
    
    private void validarIntencaoPagamento(IntencaoPagamento intencaoPagamento) {
        if (intencaoPagamento.getAmount() == null) {
            throw new IllegalArgumentException("O valor do pagamento é obrigatório");
        }
        
        if (intencaoPagamento.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("O valor do pagamento deve ser maior que zero");
        }

        if (intencaoPagamento.getMerchantRef() == null || intencaoPagamento.getMerchantRef().isEmpty()) {
            throw new IllegalArgumentException("A referência do merchant é obrigatória");
        }

        if (intencaoPagamento.getMerchantSession() == null || intencaoPagamento.getMerchantSession().isEmpty()) {
            throw new IllegalArgumentException("A sessão do merchant é obrigatória");
        }

        if (intencaoPagamento.getPosId() == null || intencaoPagamento.getPosId().isEmpty()) {
            throw new IllegalArgumentException("O ID do POS é obrigatório");
        }

        if (intencaoPagamento.getTransactionCode() == null || intencaoPagamento.getTransactionCode().isEmpty()) {
            throw new IllegalArgumentException("O código da transação é obrigatório");
        }

        // Verificar se há conta de pagamento ou informações para criar uma
        if (intencaoPagamento.getContaPagamento() == null && 
            (intencaoPagamento.getContaPagamentoEmail() == null || intencaoPagamento.getContaPagamentoEmail().isEmpty())) {
            throw new IllegalArgumentException("A conta de pagamento ou informações para criação são obrigatórias");
        }
    }
}
