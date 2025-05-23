package cv.ntinnovation.vintipay.adapter.in.web.dto;

import lombok.Data;

@Data
public class UserPerfilRequest {
    private String nome;
    private String telefone;
    private String nif;
    
    // Preferências de notificação
    private boolean notificacaoEmail;
    private boolean notificacaoSMS;
    private boolean notificacaoPush;
    private boolean notificacaoTransacoes;
    private boolean notificacaoPromocoes;
    private boolean notificacaoSeguranca;
}
