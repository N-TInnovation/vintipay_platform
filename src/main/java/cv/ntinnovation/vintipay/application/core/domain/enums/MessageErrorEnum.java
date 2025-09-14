package cv.ntinnovation.vintipay.application.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum MessageErrorEnum {

    ERROR_0(0, "Erro indefinido"),
    ERROR_1(1, "Ocorreu um erro durante o processamento da transação.\n" +
            "Por favor, tente novamente."),
    ERROR_2(2, "A autenticação segura 3D falhou."),
    ERROR_3(3, "Ocorreu um erro ao processar a transação."),
    ERROR_4(4, "Dados inválidos. Por favor, tente novamente.");

    private final int codigo;
    @Getter
    private final String mensagem;

    @JsonValue
    public int getCodigo() {
        return this.codigo;
    }

    public static MessageErrorEnum fromCodigo(int codigo) {
        for (MessageErrorEnum messageErrorEnum : MessageErrorEnum.values()) {
            if (messageErrorEnum.getCodigo() == codigo) {
                return messageErrorEnum;
            }
        }
        return ERROR_0;
    }
}
