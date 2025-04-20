package cv.ntinnovation.gateway_payment.application.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import cv.ntinnovation.gateway_payment.application.core.domain.rules.DataFingerPrint6Response;
import cv.ntinnovation.gateway_payment.application.core.domain.rules.DataFingerPrint810MPResponse;
import cv.ntinnovation.gateway_payment.application.core.domain.rules.DataFingerPrintOutroResponse;
import cv.ntinnovation.gateway_payment.application.core.domain.rules.RetornoFingerprint;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum MessageRespTypeEnum {@JsonProperty("8")
    SUCESSO_COMPRAS("8", "Sucesso", DataFingerPrint810MPResponse.builder().build()),
    SUCESSO_RECARGA("M", "Sucesso", DataFingerPrint810MPResponse.builder().build()),
    SUCESSO_SERVICO("P", "Sucesso", DataFingerPrint810MPResponse.builder().build()),
    FALHA("6", "Falha", DataFingerPrint6Response.builder().build()),
    FALHA_OUTROS(null, "Falha", DataFingerPrintOutroResponse.builder().build());

    private String code;
    private String descricao;
    private RetornoFingerprint retornoFingerprint;

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static MessageRespTypeEnum fromCode(String code) {
        return Arrays.stream(MessageRespTypeEnum.values())
                .filter(e -> code != null && code.equalsIgnoreCase(e.code))
                .findFirst()
                .orElse(FALHA_OUTROS);
    }

}
