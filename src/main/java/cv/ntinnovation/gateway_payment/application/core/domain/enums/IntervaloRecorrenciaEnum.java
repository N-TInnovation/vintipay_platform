package cv.ntinnovation.gateway_payment.application.core.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IntervaloRecorrenciaEnum {
    MENSAL("month");

    private String interval;

    public static IntervaloRecorrenciaEnum getByInterval(String name){
        for (IntervaloRecorrenciaEnum intervaloRecorrenciaEnum :IntervaloRecorrenciaEnum.values())
            if(intervaloRecorrenciaEnum.getInterval().equals(name))
                return intervaloRecorrenciaEnum;
        return null;
    }
}
