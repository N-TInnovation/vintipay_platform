package cv.ntinnovation.vintipay.application.core.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MetodoPagamentoEnum {

    VISA_CARD("pm_card_visa"),
    CARD("card");


    private String name;
}
