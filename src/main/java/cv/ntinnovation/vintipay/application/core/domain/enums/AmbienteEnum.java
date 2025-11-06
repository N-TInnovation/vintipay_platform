package cv.ntinnovation.vintipay.application.core.domain.enums;

import cv.ntinnovation.vintipay.adapter.in.web.config.EnvironmentProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AmbienteEnum {

    DESENVOLVIMENTO("DESENVOLVIMENTO"),
    HOMOLOGACAO("HOMOLOGACAO"),
    PRODUCAO("PRODUCAO");

    @Getter
    private String ambiente;

    public static AmbienteEnum fromAmbiente(String ambiente) {
        for(AmbienteEnum a : values()) {
            if(a.getAmbiente().equals(ambiente)) {
                return a;
            }
        }
        return HOMOLOGACAO;
    }

    public String getSubmitUrl(EnvironmentProperties environmentProperties) {
        if(this == PRODUCAO) {
            return environmentProperties.getSubmitPaymentProdFormUrl();
        } else if(this == HOMOLOGACAO) {
            return environmentProperties.getSubmitPaymentQldFormUrl();
        }
        return environmentProperties.getSubmitPaymentDevFormUrl();
    }

    public String getSubmitNormalUrl(EnvironmentProperties environmentProperties) {
        if(this == PRODUCAO) {
            return environmentProperties.getSubmitPaymentNormalProdFormUrl();
        } else if(this == HOMOLOGACAO) {
            return environmentProperties.getSubmitPaymentNormalQldFormUrl();
        }
        return environmentProperties.getSubmitPaymentNormalDevFormUrl();
    }

    public String getSubmitSubscricaoUrl(EnvironmentProperties environmentProperties) {
        if(this == PRODUCAO) {
            return environmentProperties.getSubmitPaymentSubscricaoProdFormUrl();
        } else if(this == HOMOLOGACAO) {
            return environmentProperties.getSubmitPaymentSubscricaoQldFormUrl();
        }
        return environmentProperties.getSubmitPaymentSubscricaoDevFormUrl();
    }

    public String getSubmitCobrancaRecorrenteUrl(EnvironmentProperties environmentProperties) {
        if(this == PRODUCAO) {
            return environmentProperties.getSubmitPaymentCobrancaRecorrenteProdFormUrl();
        } else if(this == HOMOLOGACAO) {
            return environmentProperties.getSubmitPaymentCobrancaRecorrenteQldFormUrl();
        }
        return environmentProperties.getSubmitPaymentCobrancaRecorrenteDevFormUrl();
    }

    public String getSubmitCancelamentoSubscricaoUrl(EnvironmentProperties environmentProperties) {
        if(this == PRODUCAO) {
            return environmentProperties.getSubmitPaymentCancelamentoSubscricaoProdFormUrl();
        } else if(this == HOMOLOGACAO) {
            return environmentProperties.getSubmitPaymentCancelamentoSubscricaoQldFormUrl();
        }
        return environmentProperties.getSubmitPaymentCancelamentoSubscricaoDevFormUrl();
    }

}
