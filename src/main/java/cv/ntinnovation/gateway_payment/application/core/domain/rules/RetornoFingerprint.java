package cv.ntinnovation.gateway_payment.application.core.domain.rules;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;

public interface RetornoFingerprint {
    void validarFingerPrint(FingerPrintService fingerPrintService);
    void setPosAutCode(String posAutCode);

    Pagamento getPagamento();
    void setPagamento(Pagamento pagamento);

    String getAuthorizationCode();
    void setAuthorizationCode(String authorizationCode);

}
