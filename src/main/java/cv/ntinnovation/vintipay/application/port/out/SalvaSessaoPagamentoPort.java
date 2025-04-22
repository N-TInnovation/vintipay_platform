package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.SessaoPagamento;

public interface SalvaSessaoPagamentoPort {
    SessaoPagamento salvar(SessaoPagamento sessaoPagamento);
}
