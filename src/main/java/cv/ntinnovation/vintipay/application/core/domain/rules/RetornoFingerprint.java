package cv.ntinnovation.vintipay.application.core.domain.rules;

import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;

public interface RetornoFingerprint {
    void validarFingerPrint(FingerPrintService fingerPrintService);
    void setPosAutCode(String posAutCode);

    Pagamento getPagamento();
    void setPagamento(Pagamento pagamento);

    String getAuthorizationCode();
    void setAuthorizationCode(String authorizationCode);

}
