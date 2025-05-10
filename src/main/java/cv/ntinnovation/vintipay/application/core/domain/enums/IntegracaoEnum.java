package cv.ntinnovation.vintipay.application.core.domain.enums;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.rules.integracao.Integracao;
import cv.ntinnovation.vintipay.application.core.domain.rules.integracao.VendusErp;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public enum IntegracaoEnum {

    VENDUS("vendus", new VendusErp(), "https://code-hack.vendus.cv/m-img/logo_cegid_vendus.svg");

    @Getter
    private String name;
    @Getter
    private Integracao integracao;
    @Getter
    private String logoUrl;

    public void processar(ConfiguracaoPagamento configuracao, Pagamento pagamento) {
        integracao.processar(configuracao, pagamento);
    }

    public static IntegracaoEnum getByName(String name) {
        for (IntegracaoEnum integracaoEnum :IntegracaoEnum.values())
            if(integracaoEnum.getName().equals(name))
                return integracaoEnum;
        return VENDUS;
    }

}
