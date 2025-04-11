package cv.ntinnovation.gateway_payment.application.port.out;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;

public interface SalvaPagamento {
    Pagamento salvar(Pagamento pagamento);
}
