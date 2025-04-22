package cv.ntinnovation.vintipay.application.core.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ShAccAgeIndEnum {

    SEM_CONTA("01", "Sem Conta"),
    DURANTE_TRANSACAO("02", "Criado durante a transação"),
    MENOS_30("03", "Menos de 30 dias"),
    ENTRE_30_60("04", "Entre 30 e 60 dias"),
    MAIS_60("05", "Mais de 60 dias");

    private final String ageInd;
    private final String description;
}
