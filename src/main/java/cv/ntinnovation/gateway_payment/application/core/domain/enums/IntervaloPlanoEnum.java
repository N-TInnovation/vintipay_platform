package cv.ntinnovation.gateway_payment.application.core.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IntervaloPlanoEnum {
    MENSAL("month");

    private String interval;

    public static IntervaloPlanoEnum getByInterval(String name){
        for (IntervaloPlanoEnum intervaloPlanoEnum :IntervaloPlanoEnum.values())
            if(intervaloPlanoEnum.getInterval().equals(name))
                return intervaloPlanoEnum;
        return null;
    }
}
