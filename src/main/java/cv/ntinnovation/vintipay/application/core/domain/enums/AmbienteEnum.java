package cv.ntinnovation.vintipay.application.core.domain.enums;

import cv.ntinnovation.vintipay.adapter.in.web.config.EnvironmentProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AmbienteEnum {

    PRODUCAO("PRODUCAO"),
    HOMOLOGACAO("HOMOLOGACAO"),
    DESENVOLVIMENTO("DESENVOLVIMENTO");

    @Getter
    private String ambiente;

    public static AmbienteEnum fromAmbiente(String ambiente) {
        for(AmbienteEnum a : values()) {
            if(a.getAmbiente().equals(ambiente)) {
                return a;
            }
        }
        return DESENVOLVIMENTO;
    }

    public String getSubmitUrl(EnvironmentProperties environmentProperties) {
        if(this == PRODUCAO) {
            return environmentProperties.getSubmitPaymentProdFormUrl();
        } else if(this == HOMOLOGACAO) {
            return environmentProperties.getSubmitPaymentQldFormUrl();
        }
        return environmentProperties.getSubmitPaymentDevFormUrl();
    }

}
