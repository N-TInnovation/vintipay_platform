package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.LinkPagamento;

public interface SalvaLinkPagamento {
    LinkPagamento salvar(LinkPagamento linkPagamento);
}
