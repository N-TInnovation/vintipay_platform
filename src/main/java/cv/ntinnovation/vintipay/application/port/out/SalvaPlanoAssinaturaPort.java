package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.PlanoAssinatura;

public interface SalvaPlanoAssinaturaPort {
    PlanoAssinatura salvar(PlanoAssinatura planoAssinatura);
}
