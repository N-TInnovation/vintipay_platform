package cv.ntinnovation.gateway_payment.adapter.out.db.adapter;

import cv.ntinnovation.gateway_payment.adapter.out.db.mapper.PagamentoMapper;
import cv.ntinnovation.gateway_payment.adapter.out.db.repository.PagamentoRepository;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.rules.FingerPrintService;
import cv.ntinnovation.gateway_payment.application.port.out.SalvaPagamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PagamentoAdapter implements SalvaPagamento {

    private final PagamentoRepository pagamentoRepository;
    private final PagamentoMapper pagamentoMapper;
    private final FingerPrintService fingerPrintService;

    @Override
    public Pagamento salvar(Pagamento pagamento) {
        var fingerprint = pagamento.getIntencaoPagamento().getPosId()+"|"+pagamento.getIntencaoPagamento().getAmount().longValue()+"|"+pagamento.getIntencaoPagamento().getTransactionCode()+"|"+pagamento.getIntencaoPagamento().getReference()+"|"+pagamento.getIntencaoPagamento().getContaPagamento().getEmail()+"|"+pagamento.getIntencaoPagamento().getContaPagamento().getMobilePhone();
        var authorizationCode = fingerPrintService.encryptAES256(fingerprint);
        pagamento.setAuthorizationCode(authorizationCode);
        var entity = pagamentoMapper.toEntity(pagamento);
        var saved = pagamentoRepository.save(entity);
        return pagamentoMapper.toDomain(saved);
    }
}
