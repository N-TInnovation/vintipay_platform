package cv.ntinnovation.vintipay.application.port.in;


import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;

public interface EstornarPagamentoUseCase {
    Pagamento estornar(String reference);
}
