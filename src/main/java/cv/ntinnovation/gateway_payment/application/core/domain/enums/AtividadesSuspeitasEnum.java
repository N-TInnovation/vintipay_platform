package cv.ntinnovation.gateway_payment.application.core.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AtividadesSuspeitasEnum {

    NAO_SUSPEITO("01","Não Suspeito"),
    SUSPEITO( "02","Suspeito");

    private final String activityInd;
    private final String description;
}
