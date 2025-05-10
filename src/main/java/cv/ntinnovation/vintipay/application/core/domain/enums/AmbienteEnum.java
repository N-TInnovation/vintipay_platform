package cv.ntinnovation.vintipay.application.core.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AmbienteEnum {

    PRODUCAO("PRODUCAO"),
    HOMOLOGACAO("HOMOLOGACAO"),
    DESENVOLVIMENTO("DESENVOLVIMENTO");

    @Getter
    private String ambiente;

    public static AmbienteEnum getAmbiente(String ambiente) {
        for(AmbienteEnum a : values()) {
            if(a.getAmbiente().equals(ambiente)) {
                return a;
            }
        }
        return DESENVOLVIMENTO;
    }
}
