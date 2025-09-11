package cv.ntinnovation.vintipay.application.core.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IntencaoPagamentoEnum {

    COMPRAS("1","compras","Compras"),
    RECARGA("3","recarga","Recarga"),
    PAGAMENTO_SERVICO("2","servico","Pagamento de Serviço"),
    ESTORNO("4","estorno","Estorno");

    private final String transactionCode;
    private final String intencaoPagamento;
    private final String descricao;

    public static IntencaoPagamentoEnum findByIntencaoPagamento(String intencaoPagamento) {
        for (IntencaoPagamentoEnum intencaoPagamentoEnum : IntencaoPagamentoEnum.values()) {
            if (intencaoPagamentoEnum.getIntencaoPagamento().equals(intencaoPagamento)) {
                return intencaoPagamentoEnum;
            }
        }
        return null;
    }
}
