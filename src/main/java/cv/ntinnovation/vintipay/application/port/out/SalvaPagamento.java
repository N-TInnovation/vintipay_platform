package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;

public interface SalvaPagamento {
    Pagamento salvar(Pagamento pagamento);
}
