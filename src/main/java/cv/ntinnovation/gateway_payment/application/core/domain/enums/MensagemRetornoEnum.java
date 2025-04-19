package cv.ntinnovation.gateway_payment.application.core.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MensagemRetornoEnum {

    TRANSACAO_COMPRAS_SUCESSO(MessageRespTypeEnum.SUCESSO_COMPRAS, "C", "Transação realizada com sucesso", StatusPagamentoEnum.PAGO),
    TRANSACAO_RECARGA_SUCESSO(MessageRespTypeEnum.SUCESSO_RECARGA, "C", "Transação realizada com sucesso", StatusPagamentoEnum.PAGO),
    TRANSACAO_SERVICO_SUCESSO(MessageRespTypeEnum.SUCESSO_SERVICO, "C", "Transação realizada com sucesso", StatusPagamentoEnum.PAGO),
    TRANSACAO_FALHA(MessageRespTypeEnum.FALHA, "", "Transação falhou", StatusPagamentoEnum.ERRO),
    TRANSACAO_CANCELADA(MessageRespTypeEnum.FALHA_OUTROS, "", "Transação cancelada pelo usuário", StatusPagamentoEnum.CANCELADO);

    private MessageRespTypeEnum messageType;
    private String merchantResp;
    private String descricao;
    private StatusPagamentoEnum status;


    public static MensagemRetornoEnum getMensagemRetornoEnum(MessageRespTypeEnum messageType, String merchantResp, Boolean cancelled) {
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
