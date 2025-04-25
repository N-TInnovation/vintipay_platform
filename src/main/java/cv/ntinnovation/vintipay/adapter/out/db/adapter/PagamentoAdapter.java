package cv.ntinnovation.vintipay.adapter.out.db.adapter;

import cv.ntinnovation.vintipay.adapter.out.db.mapper.PagamentoMapper;
import cv.ntinnovation.vintipay.adapter.out.db.repository.PagamentoRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.rules.FingerPrintService;
import cv.ntinnovation.vintipay.application.port.out.SalvaPagamento;
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
        var fingerprint = pagamento.getIntencaoPagamento().getPosId()+"|"+pagamento.getIntencaoPagamento().getValor().longValue()+"|"+pagamento.getIntencaoPagamento().getTransactionCode()+"|"+pagamento.getIntencaoPagamento().getReferencia()+"|"+pagamento.getIntencaoPagamento().getContaPagamento().getEmail()+"|"+pagamento.getIntencaoPagamento().getContaPagamento().getMobilePhone();
        var authorizationCode = fingerPrintService.encryptAES256(fingerprint);
        pagamento.setAuthorizationCode(authorizationCode);
        var entity = pagamentoMapper.toEntity(pagamento);
        var saved = pagamentoRepository.save(entity);
        return pagamentoMapper.toDomain(saved);
    }
}
