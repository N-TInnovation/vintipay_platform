package cv.ntinnovation.gateway_payment.application.core.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MoedaEnum {
    DOLAR("usd"),
    EURO("eur"),
    ESCUDOS("cve");

    private String name;

    public static MoedaEnum getByName(String name){
        for (MoedaEnum moedaEnum :MoedaEnum.values())
            if(moedaEnum.getName().equals(name))
                return moedaEnum;
        return null;
    }

    public boolean isCVE(){
        return this.equals(MoedaEnum.ESCUDOS);
    }
}
