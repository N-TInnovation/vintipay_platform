package cv.ntinnovation.vintipay.application.core.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusSubscricaoPagamentoEnum {

    INSCRICAO_PENDENTE("INSCRICAO_PENDENTE", "retorno/pendente"),
    INSCRICAO_CRIADA("INSCRICAO_CRIADA", "retorno/pendente"),
    INSCRICAO_CANCELADA("INSCRICAO_CANCELADA", "retorno/pendente"),
    INSCRICAO_EXPIRADA("INSCRICAO_EXPIRADA", "retorno/pendente"),
    INSCRICAO_ERRO("INSCRICAO_ERRO", "retorno/pendente"),
    INSCRICAO_SUCESSO("INSCRICAO_SUCESSO", "retorno/pendente");

    private final String status;
    private final String retorno;
}
