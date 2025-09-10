package cv.ntinnovation.vintipay.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import cv.ntinnovation.vintipay.application.core.domain.enums.MessageRespTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SispWebhookRequest {
    private String merchantRespMerchantRef; // Referência do pagamento
    private String merchantRespMerchantSession; // Sessão do cliente
    private String merchantRespErrorCode; // Código de erro
    private String merchantRespErrorDescription; // Descrição do erro
    private String merchantRespErrorDetail; // Detalhe do erro
    private String merchantRespAdditionalErrorMessage; // Mensagem de erro para o cliente
    private String merchantRespCP; // Período Contabilístico
    private String merchantRespTid; // Terminal ID
    private String merchantRespPan; // PAN truncado (***********1234)
    private BigDecimal merchantRespPurchaseAmount; // Montante da transação
    private String merchantRespMessageID; // Identificador de mensagem (20A)
    private String merchantResp; // Código de resposta (1A) - C=sucesso, Null=erro
    private String languageMessages; // Idioma (2A) - PT ou EN
    private MessageRespTypeEnum messageType; // Tipo de mensagem (1A) - 6/8/P/M
    private String merchantRespTimeStamp; // Timestamp da resposta (19A)
    private String merchantRespReloadCode; // Código de Recarga (12N)
    private String merchantRespClientReceipt; // Recibo da transação (400A)
    private String merchantRespScreenError; // Mensagem de erro para tela (60A)
    private String merchantRespEntityCode; // Código de Entidade do Serviço (5N)
    private String merchantRespReferenceNumber; // Referência de Serviço (9N)
    private String resultFingerPrint; // Hash alfanumérico (44A)
    private String resultFingerPrintVersion; // Versão do hash (1A)
    private Boolean userCancelled;
    private String dcc;
    private String dccAmount;
    private String dccCurrency;
    private String dccMarkup;
    private String dccRate;
}
