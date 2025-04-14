package cv.ntinnovation.gateway_payment.application.core.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MensagemRetornoEnum {

    TRANSACAO_COMPRAS_SUCESSO("8", "C", "Transação realizada com sucesso", StatusPagamentoEnum.CONCLUIDO),
    TRANSACAO_RECARGA_SUCESSO("M", "C", "Transação realizada com sucesso", StatusPagamentoEnum.CONCLUIDO),
    TRANSACAO_SERVICO_SUCESSO("P", "C", "Transação realizada com sucesso", StatusPagamentoEnum.CONCLUIDO),
    TRANSACAO_FALHA("", "", "Transação falhou", StatusPagamentoEnum.FALHA),
    TRANSACAO_CANCELADA("", "", "Transação cancelada", StatusPagamentoEnum.CANCELADO);

    private String messageType;
    private String merchantResp;
    private String descricao;
    private StatusPagamentoEnum status;

    public static Boolean isSucesso(String messageType, String merchantResp) {
        for (MensagemRetornoEnum mensagemRetornoEnum : MensagemRetornoEnum.values()) {
            if (mensagemRetornoEnum.getMessageType().equals(messageType) && mensagemRetornoEnum.getMerchantResp().equals(merchantResp)) {
                return true;
            }
        }
        return false;
    }

    public static MensagemRetornoEnum getMensagemRetornoEnum(String messageType, String merchantResp, Boolean cancelled) {
        if(cancelled != null && cancelled) {
            return TRANSACAO_CANCELADA;
        }
        for (MensagemRetornoEnum mensagemRetornoEnum : MensagemRetornoEnum.values()) {
            if (mensagemRetornoEnum.getMessageType().equals(messageType) && mensagemRetornoEnum.getMerchantResp().equals(merchantResp)) {
                return mensagemRetornoEnum;
            }
        }
        return TRANSACAO_FALHA;
    }
}
