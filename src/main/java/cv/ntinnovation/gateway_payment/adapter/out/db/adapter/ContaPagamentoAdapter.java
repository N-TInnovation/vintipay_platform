package cv.ntinnovation.gateway_payment.adapter.out.db.adapter;

import cv.ntinnovation.gateway_payment.adapter.out.db.entity.ContaPagamentoEntity;
import cv.ntinnovation.gateway_payment.adapter.out.db.mapper.ContaPagamentoMapper;
import cv.ntinnovation.gateway_payment.adapter.out.db.repository.ContaPagamentoRepository;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.gateway_payment.application.port.out.BuscaContaPagamentoPorEmail;
import cv.ntinnovation.gateway_payment.application.port.out.SalvaContaPagamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContaPagamentoAdapter implements BuscaContaPagamentoPorEmail, SalvaContaPagamento {

    private final ContaPagamentoRepository repository;
    private final ContaPagamentoMapper mapper;

    @Override
    public ContaPagamento buscarPorEmail(String email) {
        return mapper.toContaPagamento(repository.findByEmail(email).orElse(null));
    }

    @Override
    public ContaPagamento salvar(ContaPagamento contaPagamento) {
        // Verificar se já existe uma conta com o mesmo email
        ContaPagamentoEntity existente = repository.findByEmail(contaPagamento.getEmail()).orElse(null);
        
        if (existente != null && !existente.getId().equals(contaPagamento.getId())) {
            throw new IllegalStateException("Já existe uma conta de pagamento com este email");
        }

        ContaPagamentoEntity entity = mapper.toContaPagamentoEntity(contaPagamento);
        ContaPagamentoEntity saved = repository.save(entity);
        return mapper.toContaPagamento(saved);
    }
}
