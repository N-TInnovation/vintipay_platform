package cv.ntinnovation.gateway_payment.application.core.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MoedaEnum {
    DOLAR("USD", "840"),
    EURO("EUR", "978"),
    ESCUDOS("CVE", "132");

    private String name;
    private String codigoISO;

    public static MoedaEnum getByName(String name){
        for (MoedaEnum moedaEnum :MoedaEnum.values())
            if(moedaEnum.getName().equals(name))
                return moedaEnum;
        return ESCUDOS;
    }

    public boolean isCVE(){
        return this.equals(MoedaEnum.ESCUDOS);
    }
}
